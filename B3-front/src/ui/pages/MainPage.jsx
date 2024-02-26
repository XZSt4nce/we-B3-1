import React, {useContext, useEffect} from 'react';
import {Context} from "../../core/ContextWrapper";
import {useHistory} from "react-router-dom/cjs/react-router-dom";
import {Profile} from "../components/Profile";
import {ProductsList} from "../components/ProductsList";
import {OrdersList} from "../components/OrdersList";
import {UserList} from "../components/UserList";
import {CreateProductForm} from "../components/CreateProductForm";
import {Market} from "../components/Market";

const MainPage = () => {
    const {user, products, orders} = useContext(Context);
    const nav = useHistory();

    useEffect(() => {
        if (!user.login) {
            nav.push("/");
        }
    }, [user]);

    return !!user.login && (
        <>
            <Profile />
            {user.role !== "SUPPLIER" && <Market />}
            {user.role === "OPERATOR" && (
                <>
                    <ProductsList
                        title={"Заявки на создание продуктов"}
                        products={products.filter(product => !product.confirmed)} />
                    <UserList />
                    <OrdersList orders={orders}/>
                </>
            )}
            {user.role === "SUPPLIER" && (
                <CreateProductForm />
            )}
        </>
    );
};

export default MainPage;