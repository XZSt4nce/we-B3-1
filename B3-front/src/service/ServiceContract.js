import ServiceRequest from "./ServiceRequest";

class ServiceContract {
    async signUp(registrationDTO) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "signUp",
                "key": "action"
            },
            {
                "type": "string",
                "value": JSON.stringify(registrationDTO),
                "key": "registrationDTO"
            }
        ])
    }

    async activateUser(activationDTO) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "activateUser",
                "key": "action"
            },
            {
                "type": "string",
                "value": JSON.stringify(activationDTO),
                "key": "activationDTO"
            }
        ])
    }

    async blockUser(blockDTO) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "blockUser",
                "key": "action"
            },
            {
                "type": "string",
                "value": JSON.stringify(blockDTO),
                "key": "blockDTO"
            }
        ])
    }

    async createProduct(creationDTO) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "createProduct",
                "key": "action"
            },
            {
                "type": "string",
                "value": JSON.stringify(creationDTO),
                "key": "creationDTO"
            }
        ])
    }

    async confirmProduct(confirmationDTO) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "confirmProduct",
                "key": "action"
            },
            {
                "type": "string",
                "value": JSON.stringify(confirmationDTO),
                "key": "confirmationDTO"
            }
        ])
    }

    async makeOrder(makeDTO) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "makeOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": JSON.stringify(makeDTO),
                "key": "makeDTO"
            }
        ])
    }

    async clarifyOrder(clarifyDTO) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "clarifyOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": JSON.stringify(clarifyDTO),
                "key": "clarifyDTO"
            }
        ])
    }

    async confirmOrCancelOrder(confirmOrCancelDTO) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "confirmOrCancelOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": JSON.stringify(confirmOrCancelDTO),
                "key": "confirmOrCancelDTO"
            }
        ])
    }

    async payOrder(paymentDTO) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "payOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": JSON.stringify(paymentDTO),
                "key": "paymentDTO"
            }
        ])
    }

    async completeOrder(completionDTO) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "completeOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": JSON.stringify(completionDTO),
                "key": "completionDTO"
            }
        ])
    }

    async takeOrder(takeDTO) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "takeOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": JSON.stringify(takeDTO),
                "key": "takeDTO"
            }
        ])
    }
}

export default new ServiceContract();