#pragma once

#include <AudioClient.h>
#include <mmdeviceapi.h>
#include <initguid.h>
#include <guiddef.h>
#include <mfapi.h>

#include <wrl\implements.h>
#include <wil\com.h>
#include <wil\result.h>

#include "Common.h"
#include "Queue.h"

using namespace Microsoft::WRL;

typedef void (__stdcall * AudioCallback)(BYTE*,DWORD length);

typedef struct AudioData{
public:
    BYTE* data{};
    DWORD length{};
    bool ReleaseData(){
        try {
            if (!released.load() && this->data){
                delete[](data);
                released.exchange(true);
                data = nullptr;
                delete this;
                return true;
            }
        } catch (std::exception& e) {
            std::cout << "Illegal address release " << e.what() << std::endl;
        }
        return false;
    };
private:
    std::atomic<bool> released = {false};
} AudioData;

class CLoopbackCapture :
    public RuntimeClass< RuntimeClassFlags< ClassicCom >, FtmBase, IActivateAudioInterfaceCompletionHandler >
{
public:
    CLoopbackCapture();

    CLoopbackCapture(CLoopbackCapture const &capture);

    ~CLoopbackCapture() override;
    Queue<AudioData*> m_audioDataQueue;
    HRESULT StartCaptureAsync(DWORD processId, bool includeProcessTree);
    HRESULT StopCaptureAsync();
    bool IsRunning(){
        return this->m_running.load();
    };
    AudioData* GetNextAudioData();
    void AddAudioCallback(AudioCallback callback){
        this->m_audioCallbacks.push_back(callback);
    };
    void SetAudioFormat(WORD channels, DWORD sampleRate, WORD bitsPerSample);

    METHODASYNCCALLBACK(CLoopbackCapture, StartCapture, OnStartCapture);
    METHODASYNCCALLBACK(CLoopbackCapture, StopCapture, OnStopCapture);
    METHODASYNCCALLBACK(CLoopbackCapture, SampleReady, OnSampleReady);
    METHODASYNCCALLBACK(CLoopbackCapture, FinishCapture, OnFinishCapture);

    // IActivateAudioInterfaceCompletionHandler
    STDMETHOD(ActivateCompleted)(IActivateAudioInterfaceAsyncOperation* operation);

    WAVEFORMATEX m_CaptureFormat{};
private:
    // NB: All states >= Initialized will allow some methods
        // to be called successfully on the Audio Client
    enum class DeviceState
    {
        Uninitialized,
        Error,
        Initialized,
        Starting,
        Capturing,
        Stopping,
        Stopped,
    };

    HRESULT OnStartCapture(IMFAsyncResult* pResult);
    HRESULT OnStopCapture(IMFAsyncResult* pResult);
    HRESULT OnFinishCapture(IMFAsyncResult* pResult);
    HRESULT OnSampleReady(IMFAsyncResult* pResult);

    HRESULT InitializeLoopbackCapture();
//    HRESULT CreateWAVFile();
//    HRESULT FixWAVHeader();
    HRESULT OnAudioSampleRequested();

    HRESULT ActivateAudioInterface(DWORD processId, bool includeProcessTree);
    HRESULT FinishCaptureAsync();

    HRESULT SetDeviceStateErrorIfFailed(HRESULT hr);

    wil::com_ptr_nothrow<IAudioClient> m_AudioClient;
    UINT32 m_BufferFrames = 0;
    wil::com_ptr_nothrow<IAudioCaptureClient> m_AudioCaptureClient;
    wil::com_ptr_nothrow<IMFAsyncResult> m_SampleReadyAsyncResult;

    wil::unique_event_nothrow m_SampleReadyEvent;
    MFWORKITEM_KEY m_SampleReadyKey = 0;
    wil::critical_section m_CritSec;
    DWORD m_dwQueueID = 0;
    DWORD m_cbDataSize = 0;

    // These two members are used to communicate between the main thread
    // and the ActivateCompleted callback.
    HRESULT m_activateResult = E_UNEXPECTED;

    DeviceState m_DeviceState{ DeviceState::Uninitialized };
    wil::unique_event_nothrow m_hActivateCompleted;
    wil::unique_event_nothrow m_hCaptureStopped;

    std::atomic<bool> m_running;
    std::thread m_loopThread;
    std::vector<AudioCallback> m_audioCallbacks;
    AudioData m_emptyData = {};


};
