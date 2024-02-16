import React, {useContext} from 'react';
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {Context} from "../../core/ContextWrapper";
import {ProductsList} from "./ProductsList";
import {OrdersList} from "./OrdersList";
import {UserRole} from "../../constants/UserRole";

export const Profile = () => {
    const {user, products, orders} = useContext(Context);

    return (
        <WhiteBlock title={"Профиль"}>
            <p>Логин: {user.login}</p>
            <p>Баланс: {user.balance.toLocaleString()}</p>
            <p>ФИО: {user.fullName}</p>
            <p>Почта: {user.email}</p>
            <p>Регион(-ы): {user.regions.join(", ")}</p>
            <p>Роль: {UserRole[user.role]}</p>
            <p>{user.activated ? "Активирован" : "Не активирован"}</p>
            {user.blocked && (<p>Заблокирован</p>)}
            {user.role !== "CLIENT" && <ProductsList products={user.productsProvided.map(product => products[product])}/>}
            {user.role !== "SUPPLIER" && <ProductsList products={Object.keys(user.products).map(product => products[product])} amounts={user.products} title={"Склад"}/>}
            <OrdersList orders={user.orders.map(order => orders[order])} />
        </WhiteBlock>
    );
};