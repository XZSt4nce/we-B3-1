import {contractAddress} from "../constants/ContractAddress";

class ServiceRequest {
    url = "http://localhost:6882";
    nodeBlockchainAddress = "3NeX8hQVnk7jA6x8zuN5Rng23JrcwAxavim";
    nodeKeypairPassword = "I9ru6UtlmXBRlq6CsrSq5w";

    async #get(endpoint) {
        try {
            const response = await fetch(`${this.url}/${endpoint}`);
            if (response.ok) {
                return (await response).json();
            }
        } catch (e) {
            console.error("e");
        }
    }

    async #post(endpoint, body={}) {
        try {
            const response = await fetch(`${this.url}/${endpoint}`, {
                method: 'POST',
                headers: {
                    "Content-type": "application/json"
                },
                body: JSON.stringify(body)
            });
            if (response.ok) {
                return (await response).json();
            }
        } catch (e) {
            console.error(e);
        }
    }

    async getContractKey(key) {
        return await this.#get(`contracts/${contractAddress}/${key}`);
    }

    async getContractValues() {
        return await this.#get(`contracts/${contractAddress}`)
    }

    async getUnconfirmedTransaction(txID) {
        return await this.#get(`transactions/unconfirmed/info/${txID}`);
    }

    async signAndBroadcast(params={}) {
        return await this.#post("transactions/signAndBroadcast", {
            "contractId": contractAddress,
            "fee": 0,
            "sender": this.nodeBlockchainAddress,
            "password": this.nodeKeypairPassword,
            "type": 104,
            "params": params,
            "version": 2,
            "contractVersion": 1
        })
    }
}

export default new ServiceRequest();