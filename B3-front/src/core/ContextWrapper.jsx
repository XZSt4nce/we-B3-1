import React, {createContext, useState} from 'react';
import ServiceRequest from "../service/ServiceRequest";
import {ContractKeys} from "../constants/ContractKeys";
import {Errors} from "../constants/Errors";
import ServiceContract from "../service/ServiceContract";

export const Context = createContext({});
export const ContextWrapper = ({children}) => {
    const [user, setUser] = useState({});
    const [password, setPassword] = useState("");
    const [users, setUsers] = useState({});
    const [products, setProducts] = useState([]);
    const [orders, setOrders] = useState([]);
    const [organizations, setOrganizations] = useState([]);
    const [actionExecuting, setActionExecuting] = useState(false);

    const getContractValues = async () => {
        function getMappingObjects (data, prefix) {
            const objectsData = {};
            Object.keys(data)
                .filter(key => key.startsWith(prefix))
                .forEach(key => {
                    if (key.length > prefix.length) {
                        objectsData[key.slice(prefix.length + 1)] = data[key];
                    } else {
                        objectsData[key] = data[key];
                    }
                });
            return objectsData;
        }

        const response = await ServiceRequest.getContractValues();
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
    }

    const signUp = async (login, password, title, description, fullName, email, regions, organizationKey) => {
        if (login in users) {
            alert(Errors.USER_ALREADY_EXIST);
        } else {
            await ServiceContract.signUp({
                login: login,
                password: password,
                title: title,
                description: description,
                fullName: fullName,
                email: email,
                regions: regions,
                organizationKey: organizationKey
            })
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, "Заявка на регистрацию в системе отправлена оператору", async () => {
                            await updateUser(login);
                            await updateOrganizations();
                        });
                    }
                });
        }
    };

    const activateUser = async (userPublicKey, fullName, email, regions) => {
        if (userPublicKey in users) {
            await ServiceContract.activateUser({
                sender: user.login,
                password: password,
                userPublicKey: userPublicKey,
                fullName: fullName,
                email: email,
                regions: regions
            })
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, "Пользователь активирован", async () => await updateUser(userPublicKey));
                    }
                });
        } else {
            alert(Errors.USER_NOT_FOUND);
        }
    };

    const signIn = async (login, password) => {
        if (login in users) {
            const selectedUser = users[login];
            const passwdBuffer = new TextEncoder('utf-8').encode(login + password);
            const hashBuffer = await crypto.subtle.digest('SHA-256', passwdBuffer);
            const hashArray = Array.from(new Uint8Array(hashBuffer));
            const providedPassword = hashArray.map(b => ('00' + b.toString(16)).slice(-2)).join('');

            if (selectedUser.password === providedPassword) {
                if (selectedUser.blocked) {
                    alert(Errors.USER_BLOCKED);
                } else if (!selectedUser.activated) {
                    alert(Errors.USER_NOT_ACTIVATED);
                } else {
                    setUser(selectedUser);
                    setPassword(password);
                }
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
        await ServiceContract.blockUser({
            sender: user.login,
            password: password,
            userPublicKey: userPublicKey
        })
            .then((data) => {
                if (data) {
                    waitTransaction(data.id, "Пользователь заблокирован", async () => await updateUser(userPublicKey));
                }
            });
    };

    const createProduct = async (title, description, regions) => {
        let regionsMatch = true;
        for (const region of regions) {
            if (!user.regions.includes(region)) {
                regionsMatch = false;
                break;
            }
        }

        if (regionsMatch) {
            await ServiceContract.createProduct({
                sender: user.login,
                password: password,
                title: title,
                description: description,
                regions: regions
            })
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, "Заявка на создание продукта отправлена оператору", async () => {
                            await updateProducts();
                            await updateUser(user.login);
                        });
                    }
                });
        } else {
            alert(Errors.SUPPLIER_REGIONS_NOT_MATCH);
        }
    };

    const confirmProduct = async (productKey, description, minOrderCount, maxOrderCount, distributors) => {
        if (productKey >= products.length || productKey < 0) {
            alert(Errors.PRODUCT_NOT_FOUND);
        } else {
            await ServiceContract.confirmProduct({
                sender: user.login,
                password: password,
                productKey: productKey,
                description: description,
                minOrderCount: minOrderCount,
                maxOrderCount: maxOrderCount,
                distributors: distributors
            })
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, "Продукт подтверждён", updateProducts);
                    }
                });
        }
    };

    const makeOrder = async (productKey, executorKey, count, desiredDeliveryLimit, deliveryAddress) => {
        if (productKey >= products.length || productKey < 0) {
            alert(Errors.PRODUCT_NOT_FOUND);
        } else if (!(executorKey in users)) {
            alert(Errors.USER_NOT_FOUND);
        } else {
            await ServiceContract.makeOrder({
                sender: user.login,
                password: password,
                productKey: productKey,
                executorKey: executorKey,
                count: count,
                desiredDeliveryLimit: desiredDeliveryLimit,
                deliveryAddress: deliveryAddress
            })
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, "Заказ совершён. Ожидайте уточнения данных", async () => {
                            await updateOrders();
                            await updateUser(user.login);
                            await updateUser(executorKey);
                        });
                    }
                });
        }
    };

    const clarifyOrder = async (orderKey, totalPrice, deliveryLimit, isPrepaymentAvailable) => {
        if (orderKey >= orders.length || orderKey < 0) {
            alert(Errors.ORDER_NOT_FOUND);
        } else {
            await ServiceContract.clarifyOrder({
                sender: user.login,
                password: password,
                orderKey: orderKey,
                totalPrice: totalPrice,
                deliveryLimit: deliveryLimit,
                prepaymentAvailable: isPrepaymentAvailable
            })
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, "Заказ уточнён", updateOrders);
                    }
                });
        }
    };

    const confirmOrCancelOrder = async (orderKey, isConfirm) => {
        if (orderKey >= orders.length || orderKey < 0) {
            alert(Errors.ORDER_NOT_FOUND);
        } else {
            await ServiceContract.confirmOrCancelOrder({
                sender: user.login,
                password: password,
                orderKey: orderKey,
                confirm: isConfirm
            })
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, "Успешная операция", updateOrders);
                    }
                });
        }
    };

    const payOrder = async (orderKey) => {
        if (orderKey >= orders.length || orderKey < 0) {
            alert(Errors.ORDER_NOT_FOUND);
        } else {
            await ServiceContract.payOrder({
                sender: user.login,
                password: password,
                orderKey: orderKey
            })
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, "Заказ оплачен", async () => {
                            await updateOrders();
                            await updateUser(user.login);
                            await updateUser(orders[orderKey].executorKey);
                        });
                    }
                });
        }
    };

    const completeOrder = async (orderKey) => {
        if (orderKey >= orders.length || orderKey < 0) {
            alert(Errors.ORDER_NOT_FOUND);
        } else {
            await ServiceContract.completeOrder({
                sender: user.login,
                password: password,
                orderKey: orderKey
            })
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, "Заказ выполнен", updateOrders);
                    }
                });
        }
    };

    const takeOrder = async (orderKey) => {
        if (orderKey >= orders.length || orderKey < 0) {
            alert(Errors.ORDER_NOT_FOUND);
        } else {
            await ServiceContract.takeOrder({
                sender: user.login,
                password: password,
                orderKey: orderKey
            })
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, "Заказ получен", async () => {
                            await updateOrders();
                            await updateUser(user.login);
                            await updateUser(orders[orderKey].executorKey);
                        });
                    }
                });
        }
    };

    async function waitTransaction(txID, successMsg, callback) {
        setActionExecuting(true);
        const intervalId = setInterval(async () => {
            await ServiceRequest.getUnconfirmedTransaction(txID)
                .then(async (data) => {
                    if (!data) {
                        setActionExecuting(false);
                        await ServiceRequest.getTransactionInfo(txID)
                            .then(txInfo => {
                                if (txInfo) {
                                    callback();
                                    alert(successMsg)
                                } else {
                                    alert(Errors.REQUEST_ERROR);
                                }
                            });
                        clearInterval(intervalId);
                    }
                })
                .catch(e => alert(e));
        }, 2000);
    }

    async function updateUser(userPublicKey) {
        await ServiceRequest.getContractKey(`${ContractKeys.USERS_MAPPING_PREFIX}_${userPublicKey}`)
            .then((data) => {
                if (data) {
                    const userData = JSON.parse(data.value);
                    setUsers((state) => {
                        return {...state, [userPublicKey]: userData}
                    });
                    if (user.login === userPublicKey) {
                        setUser(userData);
                    }
                }
            });
    }

    async function updateProducts() {
        await ServiceRequest.getContractKey(ContractKeys.PRODUCTS_LIST)
            .then((data) => {
                if (data) {
                    setProducts(JSON.parse(data.value));
                }
            });
    }

    async function updateOrganizations() {
        await ServiceRequest.getContractKey(ContractKeys.ORGANIZATIONS_LIST)
            .then((data) => {
                if (data) {
                    setOrganizations(JSON.parse(data.value));
                }
            });
    }

    async function updateOrders() {
        await ServiceRequest.getContractKey(ContractKeys.ORDERS_LIST)
            .then((data) => {
                if (data) {
                    setOrders(JSON.parse(data.value));
                }
            });
    }

    const values = {
        user,
        users,
        products,
        orders,
        organizations,
        actionExecuting,
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