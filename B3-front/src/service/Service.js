import {Errors} from '../constants/Errors';

class Service {
    url = "http://localhost:6882";
    nodeSender = "3Nk8KgR1jRxYyu3TA7WggwiT6fwtfPErzY3";
    nodePassword = "-sVXcpcoS3mY1aZcA9v80w";

    async get(endpoint) {
        try {
            const response = await fetch(`${this.url}/${endpoint}`);
            if (!response.ok) {
                alert(Errors.REQUEST_ERROR)
            }
            return (await response).json();
        } catch (e) {
            alert(e);
        }
    }

    async post(endpoint, body={}) {
        try {
            const response = await fetch(`${this.url}/${endpoint}`, {
                method: 'POST',
                headers: {
                    "Content-type": "application/json"
                },
                mode: "no-cors",
                body: JSON.stringify(body)
            });
            if (!response.ok) {
                alert(Errors.REQUEST_ERROR);
            }
            return (await response).json();
        } catch (e) {
            console.error(e);
            alert(e);
        }
    }

    async getContractKey(contractAddress, key) {
        return await this.get(`${this.url}}/contracts/${contractAddress}/${key}`);
    }

    async signAndBroadcast(params={}, contractAddress) {
        return await this.post("transactions/signAndBroadcast", {
            "contractId": contractAddress,
            "fee": 0,
            "sender": this.nodeSender,
            "password": this.nodePassword,
            "type": 104,
            "params": params,
            "version": 2,
            "contractVersion": 1
        })
    }
}

export default new Service();