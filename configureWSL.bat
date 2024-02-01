@ECHO OFF
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
wsl --set-default Ubuntu
wsl --install -n
wsl --set-default-version 2
wsl wget https://raw.githubusercontent.com/waves-enterprise/we-node/release-1.12/node/src/docker/docker-compose.yml