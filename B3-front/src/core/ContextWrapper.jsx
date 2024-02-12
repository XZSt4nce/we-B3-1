import React, {createContext, useState} from 'react';
import Service from "../service/Service";
import {ContractKeys} from "../constants/ContractKeys";
import {Errors} from "../constants/Errors";
import contractAddress from "../../../contractAddress.json";

export const Context = createContext({});
export const ContextWrapper = ({children}) => {
    const [contractVersion, setContractVersion] = useState(1);
    const [user, setUser] = useState({});
    const [password, setPassword] = useState("");
    const [users, setUsers] = useState({});
    const [products, setProducts] = useState([]);
    const [orders, setOrders] = useState([]);
    const [organizations, setOrganizations] = useState([]);

    const getContractValues = async () => {
        const contracts = await Service.get(`contracts`);
        const version = contracts.find(contract => contract.contractId === contractAddress).version;
        setContractVersion(version);

        const response = await Service.get(`contracts/${contractAddress}`);
        const data = {};
        response.forEach(el => {
            try {
                data[el.key] = JSON.parse(el.value);
            } catch {
                data[el.key] = el.value;
            }
        });

        setUsers(getMappingObjects(data, ContractKeys.USERS_MAPPING_PREFIX));
        setProducts(getListObjects(data[ContractKeys.PRODUCTS_LIST]));
        setOrders(getListObjects(data[ContractKeys.ORDERS_LIST]));
        setOrganizations(getListObjects(data[ContractKeys.ORGANIZATIONS_LIST]));
    };

    const signUp = async (login, password, title, description, fullName, email, regions, organizationKey) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "signUp",
                "key": "action"
            },
            {
                "type": "string",
                "value": login,
                "key": "login"
            },
            {
                "type": "string",
                "value": password,
                "key": "password"
            },
            {
                "type": "string",
                "value": title,
                "key": "title"
            },
            {
                "type": "string",
                "value": description,
                "key": "description"
            },
            {
                "type": "string",
                "value": fullName,
                "key": "fullName"
            },
            {
                "type": "string",
                "value": email,
                "key": "email"
            },
            {
                "type": "string",
                "value": regions.toString(),
                "key": "regions"
            },
            {
                "type": "integer",
                "value": organizationKey,
                "key": "organizationKey"
            }
        ],  contractAddress, contractVersion);
    };

    const activateUser = async (userPublicKey, description, fullName, email, regions) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "signUpClient",
                "key": "action"
            },
            {
                "type": "string",
                "value": user.login,
                "key": "sender"
            },
            {
                "type": "string",
                "value": password,
                "key": "password"
            },
            {
                "type": "string",
                "value": userPublicKey,
                "key": "userPublicKey"
            },
            {
                "type": "string",
                "value": description,
                "key": "description"
            },
            {
                "type": "string",
                "value": fullName,
                "key": "fullName"
            },
            {
                "type": "string",
                "value": email,
                "key": "email"
            },
            {
                "type": "string",
                "value": regions.toString(),
                "key": "regions"
            }
        ], contractAddress, contractVersion);
    };

    const signIn = async (login, password) => {
        const key = `${ContractKeys.USERS_MAPPING_PREFIX}_${login}`;
        if (Object.keys(users).includes(key)) {
            if (users[key].password === await sha256(login + password)) {
                setUser(users[key]);
                setPassword(password);
            } else {
                alert(Errors.INCORRECT_LOGIN);
            }
        } else {
            alert(Errors.INCORRECT_LOGIN);
        }
    };

    const signOut = () => {
        setUser({});
        setPassword("");
    };

    const blockUser = async (userPublicKey) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "blockUser",
                "key": "action"
            },
            {
                "type": "string",
                "value": user.login,
                "key": "sender"
            },
            {
                "type": "string",
                "value": password,
                "key": "password"
            },
            {
                "type": "string",
                "value": userPublicKey,
                "key": "userPublicKey"
            }
        ], contractAddress, contractVersion);
    };

    const createProduct = async (title, description, regions) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "createProduct",
                "key": "action"
            },
            {
                "type": "string",
                "value": user.login,
                "key": "sender"
            },
            {
                "type": "string",
                "value": password,
                "key": "password"
            },
            {
                "type": "string",
                "value": title,
                "key": "title"
            },
            {
                "type": "string",
                "value": description,
                "key": "description"
            },
            {
                "type": "string",
                "value": regions.toString(),
                "key": "regions"
            }
        ], contractAddress, contractVersion);
    };

    const confirmProduct = async (productKey, description, regions, minOrderCount, maxOrderCount, distributors) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "confirmProduct",
                "key": "action"
            },
            {
                "type": "string",
                "value": user.login,
                "key": "sender"
            },
            {
                "type": "string",
                "value": password,
                "key": "password"
            },
            {
                "type": "integer",
                "value": productKey,
                "key": "productKey"
            },
            {
                "type": "string",
                "value": description,
                "key": "description"
            },
            {
                "type": "string",
                "value": regions.toString(),
                "key": "regions"
            },
            {
                "type": "integer",
                "value": minOrderCount,
                "key": "minOrderCount"
            },
            {
                "type": "integer",
                "value": maxOrderCount,
                "key": "maxOrderCount"
            },
            {
                "type": "string",
                "value": distributors.toString(),
                "key": "distributors"
            },
        ], contractAddress, contractVersion);
    };

    const makeOrder = async (productKey, organization, count, desiredDeliveryLimit, deliveryAddress) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "makeOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": user.login,
                "key": "sender"
            },
            {
                "type": "string",
                "value": password,
                "key": "password"
            },
            {
                "type": "integer",
                "value": productKey,
                "key": "productKey"
            },
            {
                "type": "string",
                "value": organization,
                "key": "organization"
            },
            {
                "type": "integer",
                "value": count,
                "key": "count"
            },
            {
                "type": "integer",
                "value": desiredDeliveryLimit,
                "key": "desiredDeliveryLimit"
            },
            {
                "type": "string",
                "value": deliveryAddress,
                "key": "deliveryAddress"
            },
        ], contractAddress, contractVersion);
    };

    const clarifyOrder = async (orderKey, totalPrice, deliveryLimit, isPrepaymentAvailable) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "clarifyOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": user.login,
                "key": "sender"
            },
            {
                "type": "string",
                "value": password,
                "key": "password"
            },
            {
                "type": "integer",
                "value": orderKey,
                "key": "orderKey"
            },
            {
                "type": "integer",
                "value": totalPrice,
                "key": "totalPrice"
            },
            {
                "type": "integer",
                "value": deliveryLimit,
                "key": "deliveryLimit"
            },
            {
                "type": "boolean",
                "value": isPrepaymentAvailable,
                "key": "isPrepaymentAvailable"
            },
        ], contractAddress, contractVersion);
    };

    const confirmOrCancelOrder = async (orderKey, isConfirm) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "confirmOrCancelOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": user.login,
                "key": "sender"
            },
            {
                "type": "string",
                "value": password,
                "key": "password"
            },
            {
                "type": "integer",
                "value": orderKey,
                "key": "orderKey"
            },
            {
                "type": "boolean",
                "value": isConfirm,
                "key": "isConfirm"
            },
        ], contractAddress, contractVersion);
    };

    const payOrder = async (orderKey) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "payOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": user.login,
                "key": "sender"
            },
            {
                "type": "string",
                "value": password,
                "key": "password"
            },
            {
                "type": "integer",
                "value": orderKey,
                "key": "orderKey"
            }
        ], contractAddress, contractVersion);
    };

    const completeOrder = async (orderKey) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "completeOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": user.login,
                "key": "sender"
            },
            {
                "type": "string",
                "value": password,
                "key": "password"
            },
            {
                "type": "integer",
                "value": orderKey,
                "key": "orderKey"
            }
        ], contractAddress, contractVersion);
    };

    const takeOrder = async (orderKey) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "takeOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": user.login,
                "key": "sender"
            },
            {
                "type": "string",
                "value": password,
                "key": "password"
            },
            {
                "type": "integer",
                "value": orderKey,
                "key": "orderKey"
            }
        ], contractAddress, contractVersion);
    };

    const getMappingObjects = (data, prefix) => {
        const objectsData = {};
        Object.keys(data)
            .filter(key => key.startsWith(prefix))
            .forEach(key => objectsData[key] = data[key]);
        return objectsData;
    };

    const getListObjects = (list) => {
        return list.map(el => JSON.parse(el));
    }

    const sha256 = async (passwd) => {
        const passwdBuffer = new TextEncoder('utf-8').encode(passwd);
        const hashBuffer = await crypto.subtle.digest('SHA-256', passwdBuffer);
        const hashArray = Array.from(new Uint8Array(hashBuffer));
        return hashArray.map(b => ('00' + b.toString(16)).slice(-2)).join('');
    }

    const values = {
        user,
        users,
        products,
        orders,
        organizations,
        getContractValues,
        signUp,
        activateUser,
        signIn,
        signOut,
        blockUser,
        createProduct,
        confirmProduct,
        makeOrder,
        clarifyOrder,
        confirmOrCancelOrder,
        payOrder,
        completeOrder,
        takeOrder,
        sha256
    };

    return (
        <Context.Provider value={values}>
            {children}
        </Context.Provider>
    );
};