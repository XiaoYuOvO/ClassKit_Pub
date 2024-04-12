//
// Created by 705 on 2022/11/27.
//

#include "CaptureLibrary.h"
#include "iostream"
#include "map"
#include "objbase.h"
#include <stdlib.h>
#include <crtdbg.h>
extern "C"{
DLLEXPORT HRESULT StartCaptureAsync(CLoopbackCapture* client, DWORD pid, bool includeProcessTree){
    std::cout << "Starting audio client" << std::endl;
    std::cout << "Client addr " << client << std::endl;
    return  client->StartCaptureAsync(pid, includeProcessTree);
}

DLLEXPORT CLoopbackCapture* CreateCaptureClient(){
    _CrtDumpMemoryLeaks();
    _CrtSetDbgFlag ( _CRTDBG_ALLOC_MEM_DF | _CRTDBG_LEAK_CHECK_DF );
    CLoopbackCapture* client;
    MakeAndInitialize<CLoopbackCapture>(&client);
    return client;
}

DLLEXPORT HRESULT StopCapture(CLoopbackCapture* client){
    return client->StopCaptureAsync();
}

DLLEXPORT AudioData* GetNextAudioData(CLoopbackCapture* client){
    return client->GetNextAudioData();
}

DLLEXPORT void ReleaseAudioData(AudioData* data){
    if(data != nullptr && data->ReleaseData()){
    }
}

DLLEXPORT void ReleaseClient(CLoopbackCapture* client){
    client->Release();
    delete client;
}

DLLEXPORT bool IsRunning(CLoopbackCapture* client){
    return client->IsRunning();
}

DLLEXPORT bool HasAudioData(CLoopbackCapture* client){
    return !client->m_audioDataQueue.empty();
}

DLLEXPORT void SetAudioFormat(CLoopbackCapture* client, WORD channels, DWORD sampleRate, WORD bitsPerSample){
    return client->SetAudioFormat(channels, sampleRate, bitsPerSample);
}

DLLEXPORT int GetAvailableData(CLoopbackCapture* client){
    return client->m_audioDataQueue.size() * (client -> m_CaptureFormat).nSamplesPerSec * (client -> m_CaptureFormat).nChannels;
}

DLLEXPORT void AddAudioCallback(){

}
}
