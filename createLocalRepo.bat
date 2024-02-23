docker run -d -p 5000:5000 --name registry registry:2.7
docker build B3-contract -f B3-contract/Dockerfile --platform linux/amd64 -t localhost:5000/b3-contract
docker tag localhost:5000/b3_contract localhost:5000/b3_contract