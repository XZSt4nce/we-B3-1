docker rm -f registry
docker run -d -p 5000:5000 --name registry registry:2.7
wsl docker build B3-contract -f B3-contract/Dockerfile --platform linux/amd64 -t localhost:5000/b3-contract:1.0.0
docker tag localhost:5000/b3-contract localhost:5000/b3-contract1.0.0
wsl docker push localhost:5000/b3-contract:1.0.0