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
                .forEach(key => objectsData[key.slice(prefix.length + 1)] = data[key]);
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
            await ServiceContract.signUp(login, password, title, description, fullName, email, regions, organizationKey)
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, async () => {
                            await updateUser(login);
                            alert("Заявка на регистрацию в системе отправлена оператору");
                        });
                    }
                });
        }
    };

    const activateUser = async (userPublicKey, description, fullName, email, regions) => {
        if (userPublicKey in users) {
            await ServiceContract.activateUser(user.login, password, userPublicKey, description, fullName, email, regions)
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, async () => await updateUser(userPublicKey));
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
        setOrders([]);
    };

    const blockUser = async (userPublicKey) => {
        await ServiceContract.blockUser(user.login, password, userPublicKey)
            .then((data) => {
                if (data) {
                    waitTransaction(data.id, async () => await updateUser(userPublicKey));
                }
            });
    };

    const createProduct = async (title, description, regions) => {
        await ServiceContract.createProduct(user.login, password, title, description, regions)
            .then((data) => {
                if (data) {
                    waitTransaction(data.id, async () => {
                        await updateProducts();
                        await updateUser(user.login);
                        alert("Заявка на создание продукта отправлена оператору");
                    });
                }
            });
    };

    const confirmProduct = async (productKey, description, regions, minOrderCount, maxOrderCount, distributors) => {
        if (productKey >= products.length) {
            alert(Errors.PRODUCT_NOT_FOUND);
        } else if (productKey < 0) {
            alert(Errors.INCORRECT_DATA);
        } else {
            await ServiceContract.confirmProduct(user.login, password, productKey, description, regions, minOrderCount, maxOrderCount, distributors)
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, updateProducts);
                    }
                });
        }
    };

    const makeOrder = async (productKey, executorKey, count, desiredDeliveryLimit, deliveryAddress) => {
        if (productKey >= products.length) {
            alert(Errors.PRODUCT_NOT_FOUND);
        } else if (productKey < 0) {
            alert(Errors.INCORRECT_DATA);
        } else if (!(executorKey in users)) {
            alert(Errors.USER_NOT_FOUND);
        } else {
            await ServiceContract.makeOrder(user.login, password, productKey, executorKey, count, desiredDeliveryLimit, deliveryAddress)
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, async () => {
                            await updateOrders();
                            await updateUser(user.login);
                            await updateUser(executorKey);
                        });
                    }
                });
        }
    };

    const clarifyOrder = async (orderKey, totalPrice, deliveryLimit, isPrepaymentAvailable) => {
        if (orderKey >= orders.length) {
            alert(Errors.ORDER_NOT_FOUND);
        } else if (orderKey < 0) {
            alert(Errors.INCORRECT_DATA);
        } else {
            await ServiceContract.clarifyOrder(user.login, password, orderKey, totalPrice, deliveryLimit, isPrepaymentAvailable)
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, updateOrders);
                    }
                });
        }
    };

    const confirmOrCancelOrder = async (orderKey, isConfirm) => {
        if (orderKey >= orders.length) {
            alert(Errors.ORDER_NOT_FOUND);
        } else if (orderKey < 0) {
            alert(Errors.INCORRECT_DATA);
        } else {
            await ServiceContract.confirmOrCancelOrder(user.login, password, orderKey, isConfirm)
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, updateOrders);
                    }
                });
        }
    };

    const payOrder = async (orderKey) => {
        if (orderKey >= orders.length) {
            alert(Errors.ORDER_NOT_FOUND);
        } else if (orderKey < 0) {
            alert(Errors.INCORRECT_DATA);
        } else {
            await ServiceContract.payOrder(user.login, password, orderKey)
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, updateOrders);
                    }
                });
        }
    };

    const completeOrder = async (orderKey) => {
        if (orderKey >= orders.length) {
            alert(Errors.ORDER_NOT_FOUND);
        } else if (orderKey < 0) {
            alert(Errors.INCORRECT_DATA);
        } else {
            await ServiceContract.completeOrder(user.login, password, orderKey)
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, updateOrders);
                    }
                });
        }
    };

    const takeOrder = async (orderKey) => {
        if (orderKey >= orders.length) {
            alert(Errors.ORDER_NOT_FOUND);
        } else if (orderKey < 0) {
            alert(Errors.INCORRECT_DATA);
        } else {
            await ServiceContract.takeOrder(user.login, password, orderKey)
                .then((data) => {
                    if (data) {
                        waitTransaction(data.id, updateOrders);
                    }
                });
        }
    };

    async function waitTransaction(txID, callback) {
        setActionExecuting(true);
        const intervalId = setInterval(async () => {
            console.log(123);
            await ServiceRequest.getUnconfirmedTransaction(txID)
                .then((data) => {
                    console.log(data);
                    if (!data) {
                        setActionExecuting(false);
                        callback();
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
                    setUsers({...users, login: data});
                }
            });
    }

    async function updateOrders() {
        await ServiceRequest.getContractKey(ContractKeys.ORDERS_LIST)
            .then((data) => {
                if (data) {
                    setOrders(data);
                }
            });
    }

    async function updateProducts(){
        await ServiceRequest.getContractKey(ContractKeys.PRODUCTS_LIST)
            .then((data) => {
                if (data) {
                    setProducts(data);
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