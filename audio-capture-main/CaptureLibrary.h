//
// Created by 19662 on 2022/11/27.
//

#ifndef AUDIO_CAPTURE_CAPTURELIBRARY_H
#define AUDIO_CAPTURE_CAPTURELIBRARY_H
#include "vector"
#include "LoopbackCapture.h"
#define  DLLEXPORT _declspec(dllexport)
using namespace std;

extern "C"{
DLLEXPORT CLoopbackCapture* CreateCaptureClient();
DLLEXPORT HRESULT StartCaptureAsync(CLoopbackCapture* captureClient, DWORD pid, bool includeProcessTree);
DLLEXPORT HRESULT StopCapture(CLoopbackCapture* client);
DLLEXPORT AudioData* GetNextAudioData(CLoopbackCapture* client);
DLLEXPORT bool IsRunning(CLoopbackCapture* client);
DLLEXPORT bool HasAudioData(CLoopbackCapture* client);
DLLEXPORT void ReleaseAudioData(AudioData* data);
DLLEXPORT void ReleaseClient(CLoopbackCapture* client);
DLLEXPORT void SetAudioFormat(CLoopbackCapture* client,WORD channels, DWORD sampleRate, WORD bitsPerSample);
DLLEXPORT int GetAvaliableData(CLoopbackCapture* client);
}
#endif //AUDIO_CAPTURE_CAPTURELIBRARY_H

