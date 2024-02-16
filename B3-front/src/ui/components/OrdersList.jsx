import React from 'react';
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {Order} from "../kit/Order";
import {EmptyListPlug} from "./EmptyListPlug";

export const OrdersList = ({orders}) => {
    return (
        <WhiteBlock title={"Заказы"}>
            {orders.length === 0 ? <EmptyListPlug /> : orders.map((order, idx) => (
                <Order order={order} key={idx} />
            ))}
        </WhiteBlock>
    );
};