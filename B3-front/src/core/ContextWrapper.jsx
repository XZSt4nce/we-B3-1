import React, {createContext, useState} from 'react';
import Service from "../service/Service";
import {ContractKeys} from "../constants/ContractKeys";
import {Errors} from "../constants/Errors";

export const Context = createContext({});
export const ContextWrapper = ({children}) => {
    const contractAddress = "5hEEZg4uimrq8R1shaFZxEWqq3wXAbGz1dWrHnCv9Suq";
    const [contractVersion, setContractVersion] = useState(1);
    const [user, setUser] = useState({});
    const [users, setUsers] = useState({});
    const [products, setProducts] = useState({});
    const [orders, setOrders] = useState({});
    const [employees, setEmployees] = useState({});

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
        setProducts(getMappingObjects(data, ContractKeys.PRODUCTS_MAPPING_PREFIX));
        setOrders(getMappingObjects(data, ContractKeys.ORDERS_MAPPING_PREFIX));
        setEmployees(getMappingObjects(data, ContractKeys.EMPLOYEES_MAPPING_PREFIX));
    };

    const signUpSupplier = async (login, title, description, fullName, email, regions, organizationKey) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "signUpSupplier",
                "key": "action"
            },
            {
                "type": "string",
                "value": login,
                "key": "login"
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
                "type": "string",
                "value": organizationKey,
                "key": "organizationKey"
            }
        ],  contractAddress, contractVersion)
            .then(async (data) => {
                if (data) {
                    await Service.get(`contracts/${contractAddress}/`)
                }
            });
    };

    const signUpDistributor = async (login, title, fullName, email, regions, organizationKey) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "signUpDistributor",
                "key": "action"
            },
            {
                "type": "string",
                "value": login,
                "key": "login"
            },
            {
                "type": "string",
                "value": title,
                "key": "title"
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
                "type": "string",
                "value": organizationKey,
                "key": "organizationKey"
            }
        ], contractAddress, contractVersion);
    };

    const signUpClient = async (login, fullName, email, region) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "signUpDistributor",
                "key": "action"
            },
            {
                "type": "string",
                "value": login,
                "key": "login"
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
                "value": region,
                "key": "regions"
            }
        ], contractAddress, contractVersion);
    };

    const activateUser = async (sender, userPublicKey, description, fullName, email, regions) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "signUpClient",
                "key": "action"
            },
            {
                "type": "string",
                "value": sender,
                "key": "sender"
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

    const signIn = (login) => {
        const key = `${ContractKeys.USERS_MAPPING_PREFIX}_${login}`;
        if (Object.keys(users).includes(key)) {
            setUser(users[key]);
        } else {
            alert(Errors.INCORRECT_LOGIN);
        }
    };

    const signOut = () => {
        setUser({});
    };

    const blockUser = async (sender, userPublicKey) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "blockUser",
                "key": "action"
            },
            {
                "type": "string",
                "value": sender,
                "key": "sender"
            },
            {
                "type": "string",
                "value": userPublicKey,
                "key": "userPublicKey"
            }
        ], contractAddress, contractVersion);
    };

    const createProduct = async (sender, title, description, regions) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "createProduct",
                "key": "action"
            },
            {
                "type": "string",
                "value": sender,
                "key": "sender"
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

    const confirmProduct = async (sender, productKey, description, regions, minOrderCount, maxOrderCount, distributors) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "confirmProduct",
                "key": "action"
            },
            {
                "type": "string",
                "value": sender,
                "key": "sender"
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

    const makeOrder = async (sender, productKey, organization, count, desiredDeliveryLimit, deliveryAddress) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "makeOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": sender,
                "key": "sender"
            },
            {
                "type": "string",
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

    const clarifyOrder = async (sender, orderKey, totalPrice, deliveryLimitUnixTime, isPrepaymentAvailable) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "clarifyOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": sender,
                "key": "sender"
            },
            {
                "type": "string",
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
                "value": deliveryLimitUnixTime,
                "key": "deliveryLimitUnixTime"
            },
            {
                "type": "boolean",
                "value": isPrepaymentAvailable,
                "key": "isPrepaymentAvailable"
            },
        ], contractAddress, contractVersion);
    };

    const confirmOrCancelOrder = async (sender, orderKey, isConfirm) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "confirmOrCancelOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": sender,
                "key": "sender"
            },
            {
                "type": "string",
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

    const payOrder = async (sender, orderKey) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "payOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": sender,
                "key": "sender"
            },
            {
                "type": "string",
                "value": orderKey,
                "key": "orderKey"
            }
        ], contractAddress, contractVersion);
    };

    const completeOrder = async (sender, orderKey) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "completeOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": sender,
                "key": "sender"
            },
            {
                "type": "string",
                "value": orderKey,
                "key": "orderKey"
            }
        ], contractAddress, contractVersion);
    };

    const takeOrder = async (sender, orderKey) => {
        await Service.signAndBroadcast([
            {
                "type": "string",
                "value": "takeOrder",
                "key": "action"
            },
            {
                "type": "string",
                "value": sender,
                "key": "sender"
            },
            {
                "type": "string",
                "value": orderKey,
                "key": "orderKey"
            }
        ], contractAddress, contractVersion)
            .then(data => {
                if (data) {
                    users[getSenderKeyOrder(sender)].products.push(orderKey);
                }
            });
    };

    const getSenderKeyOrder = (sender) => {
        return !users[sender].organizationKey ? sender : users[sender].organizationKey;
    }

    const getMappingObjects = (data, prefix) => {
        const objectsData = {};
        Object.keys(data)
            .filter(key => key.startsWith(prefix))
            .forEach(key => objectsData[key] = data[key]);
        return objectsData;
    };

    const values = {
        user,
        users,
        products,
        orders,
        employees,
        getContractValues,
        signUpSupplier,
        signUpDistributor,
        signUpClient,
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
        takeOrder
    };

    return (
        <Context.Provider value={values}>
            {children}
        </Context.Provider>
    );
};