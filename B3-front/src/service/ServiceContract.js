import ServiceRequest from "./ServiceRequest";

class ServiceContract {
    async signUp(login, password, title, description, fullName, email, regions, organizationKey) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "signUp",
                "key": "action"
            },
            {
                "type": "string",
                "value": `{"login": "${login}", "password": "${password}", "title": "${title}", "description": "${description}", "fullName": "${fullName}", "email": "${email}", "regions": ${regions}, "organizationKey": ${organizationKey}}`,
                "key": "registrationDTO"
            }
        ])
    }

    async activateUser(sender, password, userPublicKey, description, fullName, email, regions) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "signUpClient",
                "key": "action"
            },
            {
                "type": "string",
                "value": `{"sender": "${sender}","password": "${password}","userPublicKey": "${userPublicKey}","description": "${description}","fullName": "${fullName}","email": "${email}","regions": ${regions}}`,
                "key": "activationDTO"
            }
        ])
    }

    async blockUser(sender, password, userPublicKey) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "blockUser",
                "key": "action"
            },
            {
                "type": "string",
                "value": `{"sender": "${sender}","password": "${password}","userPublicKey": "${userPublicKey}"}`,
                "key": "blockDTO"
            }
        ])
    }

    async createProduct(sender, password, title, description, regions) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "createProduct",
                "key": "action"
            },
            {
                "type": "string",
                "value": `{"sender": "${sender}","password": "${password}","title": "${title}","description": "${description}","regions": ${regions}}`,
                "key": "creationDTO"
            }
        ])
    }

    async confirmProduct(sender, password, productKey, description, regions, minOrderCount, maxOrderCount, distributors) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "confirmProduct",
                "key": "action"
            },
            {
                "type": "string",
                "value": `{"sender": "${sender}","password": "${password}","productKey": ${productKey},"description": "${description}","regions": ${regions},"minOrderCount": ${minOrderCount},"maxOrderCount": ${maxOrderCount},"distributors": ${distributors}}`,
                "key": "confirmationDTO"
            }
        ])
    }

    async makeOrder(sender, password, productKey, executorKey, count, desiredDeliveryLimit, deliveryAddress) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "makeOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": `{"sender": "${sender}","password": "${password}","productKey": ${productKey},"executorKey": "${executorKey}","count": ${count},"desiredDeliveryLimit": ${desiredDeliveryLimit},"deliveryAddress": "${deliveryAddress}"}`,
                "key": "makeDTO"
            }
        ])
    }

    async clarifyOrder(sender, password, orderKey, totalPrice, deliveryLimit, isPrepaymentAvailable) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "clarifyOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": `{"sender": "${sender}","password": "${password}","orderKey": ${orderKey},"totalPrice": ${totalPrice},"deliveryLimit": ${deliveryLimit},"isPrepaymentAvailable": ${isPrepaymentAvailable}}`,
                "key": "clarifyDTO"
            }
        ])
    }

    async confirmOrCancelOrder(sender, password, orderKey, isConfirm) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "confirmOrCancelOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": `{"sender": "${sender}","password": "${password}","orderKey": ${orderKey},"isConfirm": ${isConfirm}}`,
                "key": "confirmOrCancelDTO"
            }
        ])
    }

    async payOrder(sender, password, orderKey) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "payOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": `{"sender": "${sender}","password": "${password}","orderKey": ${orderKey}}`,
                "key": "payDTO"
            }
        ])
    }

    async completeOrder(sender, password, orderKey) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "completeOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": `{"sender": "${sender}","password": "${password}","orderKey": ${orderKey}}`,
                "key": "completionDTO"
            }
        ])
    }

    async takeOrder(sender, password, orderKey) {
        return await ServiceRequest.signAndBroadcast([
            {
                "type": "string",
                "value": "takeOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": `{"sender": "${sender}","password": "${password}","orderKey": ${orderKey}}`,
                "key": "takeDTO"
            }
        ])
    }
}

export default new ServiceContract();