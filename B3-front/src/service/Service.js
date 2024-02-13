import {Errors} from '../constants/Errors';

class Service {
    url = "http://localhost:6882";
    nodeSender = "3NhhS4CVKcrjbqc3h84ZgWKDNSr2HKUdMCZ";
    nodePassword = "fL2UB3rOOADO4w6dtqNTMA";

    async get(endpoint) {
        try {
            const response = await fetch(`${this.url}/${endpoint}`);
            if (!response.ok) {
                alert(Errors.REQUEST_ERROR)
            }
            return (await response).json();
        } catch (e) {
            console.log(e);
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