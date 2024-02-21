import React, {createContext, useState} from 'react';
import Service from "../service/Service";
import {ContractKeys} from "../constants/ContractKeys";
import {Errors} from "../constants/Errors";
import {contractAddress} from "../constants/ContractAddress.js";

export const Context = createContext({});
export const ContextWrapper = ({children}) => {
    const [user, setUser] = useState({});
    const [password, setPassword] = useState("");
    const [users, setUsers] = useState({});
    const [products, setProducts] = useState([]);
    const [orders, setOrders] = useState([]);
    const [organizations, setOrganizations] = useState([]);

    const getContractValues = async () => {
        const getMappingObjects = (data, prefix) => {
            const objectsData = {};
            Object.keys(data)
                .filter(key => key.startsWith(prefix))
                .forEach(key => objectsData[key] = data[key]);
            return objectsData;
        };

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
        setProducts(data[ContractKeys.PRODUCTS_LIST]);
        setOrders(data[ContractKeys.ORDERS_LIST]);
        setOrganizations(data[ContractKeys.ORGANIZATIONS_LIST]);
    };

    const signUp = async (login, password, title, description, fullName, email, regions, organizationKey) => {
        const handler = async() => {
            if (login in users) {
                alert(Errors.USER_ALREADY_EXIST);
            } else {
                await Service.getContractKey(contractAddress, `${ContractKeys.USERS_MAPPING_PREFIX}_${login}`)
                    .then((data) => {
                        if (data) {
                            setUsers({...users, login: data});
                        }
                    });
            }
        };

        return await Service.signAndBroadcast([
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
                "value": JSON.stringify(regions),
                "key": "regions"
            },
            {
                "type": "integer",
                "value": organizationKey,
                "key": "organizationKey"
            }
        ],  contractAddress)
            .then((data) => {
                if (data) {
                    waitThreeSeconds(handler);
                }
            });
    };

    const activateUser = async (userPublicKey, description, fullName, email, regions) => {
        return await Service.signAndBroadcast([
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
                "value": JSON.stringify(regions),
                "key": "regions"
            }
        ], contractAddress)
            .then((data) => {
                if (data) {
                    waitThreeSeconds(() => updateUser(userPublicKey));
                }
            });
    };

    const signIn = async (login, password) => {
        const selectedUser = users[`${ContractKeys.USERS_MAPPING_PREFIX}_${login}`];
        if (selectedUser?.password === await sha256(login + password)) {
            if (selectedUser.blocked) {
                alert(Errors.USER_BLOCKED);
            } else {
                setUser(selectedUser);
                setPassword(password);
            }
        } else {
            alert(Errors.INCORRECT_LOGIN);
        }
    };

    const signOut = () => {
        setUser({});
        setPassword("");
        setOrders([]);
    };

    const blockUser = async (userPublicKey) => {
        return await Service.signAndBroadcast([
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
        ], contractAddress)
            .then((data) => {
                if (data) {
                    waitThreeSeconds(() => updateUser(userPublicKey));
                }
            });
    };

    const createProduct = async (title, description, regions) => {
        const handler = async () => {
            await Service.getContractKey(contractAddress, ContractKeys.PRODUCTS_LIST)
                .then((data) => {
                    if (data) {
                        setProducts(data);
                    }
                });
        };

        return await Service.signAndBroadcast([
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
                "value": JSON.stringify(regions),
                "key": "regions"
            }
        ], contractAddress)
            .then((data) => {
                if (data) {
                    waitThreeSeconds(handler);
                }
            });
    };

    const confirmProduct = async (productKey, description, regions, minOrderCount, maxOrderCount, distributors) => {
        return await Service.signAndBroadcast([
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
                "value": JSON.stringify(regions),
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
                "value": JSON.stringify(distributors),
                "key": "distributors"
            },
        ], contractAddress)
            .then((data) => {
                if (data) {
                    waitThreeSeconds(() => updateProduct(productKey));
                }
            });
    };

    const makeOrder = async (productKey, organization, count, desiredDeliveryLimit, deliveryAddress) => {
        const handler = async() => {
            await Service.getContractKey(contractAddress, ContractKeys.ORDERS_LIST)
                .then((data) => {
                    if (data) {
                        setOrders(data);
                    }
                });
        };

        return await Service.signAndBroadcast([
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
        ], contractAddress)
            .then((data) => {
                if (data) {
                    waitThreeSeconds(handler);
                }
            });
    };

    const clarifyOrder = async (orderKey, totalPrice, deliveryLimit, isPrepaymentAvailable) => {
        return await Service.signAndBroadcast([
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
        ], contractAddress)
            .then((data) => {
                if (data) {
                    waitThreeSeconds(() => updateOrder(orderKey));
                }
            });
    };

    const confirmOrCancelOrder = async (orderKey, isConfirm) => {
        return await Service.signAndBroadcast([
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
        ], contractAddress)
            .then((data) => {
                if (data) {
                    waitThreeSeconds(() => updateOrder(orderKey));
                }
            });
    };

    const payOrder = async (orderKey) => {
        return await Service.signAndBroadcast([
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
        ], contractAddress)
            .then((data) => {
                if (data) {
                    waitThreeSeconds(() => updateOrder(orderKey));
                }
            });
    };

    const completeOrder = async (orderKey) => {
        return await Service.signAndBroadcast([
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
        ], contractAddress)
            .then((data) => {
                if (data) {
                    waitThreeSeconds(() => updateOrder(orderKey));
                }
            });
    };

    const takeOrder = async (orderKey) => {
        return await Service.signAndBroadcast([
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
        ], contractAddress)
            .then((data) => {
                if (data) {
                    waitThreeSeconds(() => updateOrder(orderKey));
                }
            });
    };

    const sha256 = async (passwd) => {
        const passwdBuffer = new TextEncoder('utf-8').encode(passwd);
        const hashBuffer = await crypto.subtle.digest('SHA-256', passwdBuffer);
        const hashArray = Array.from(new Uint8Array(hashBuffer));
        return hashArray.map(b => ('00' + b.toString(16)).slice(-2)).join('');
    };

    const waitThreeSeconds = (callback) => {
        setTimeout(callback, 3000);
    };

    const updateUser = async (userPublicKey) => {
        if (userPublicKey in users) {
            await Service.getContractKey(contractAddress, `${ContractKeys.USERS_MAPPING_PREFIX}_${userPublicKey}`)
                .then((data) => {
                    if (data) {
                        setUsers({...users, login: data});
                    }
                });
        } else {
            alert(Errors.USER_NOT_FOUND);
        }
    };

    const updateOrder = async (orderKey) => {
        if (orderKey > orders.length - 1 || orderKey < 0) {
            alert(Errors.ORDER_NOT_FOUND);
        } else {
            await Service.getContractKey(contractAddress, `${ContractKeys.ORDERS_LIST}_${orderKey}`)
                .then((data) => {
                    if (data) {
                        const newOrders = [...orders];
                        newOrders[orderKey] = data;
                        setOrders(newOrders);
                    }
                });
        }
    };

    const updateProduct = async (productKey) => {
        if (productKey > products.length - 1 || productKey < 0) {
            alert(Errors.PRODUCT_NOT_FOUND);
        } else {
            await Service.getContractKey(contractAddress, `${ContractKeys.PRODUCTS_LIST}_${productKey}`)
                .then((data) => {
                    if (data) {
                        const newProducts = [...products];
                        newProducts[productKey] = data;
                        setOrders(newProducts);
                    }
                });
        }
    };

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
        takeOrder
    };

    return (
        <Context.Provider value={values}>
            {children}
        </Context.Provider>
    );
};