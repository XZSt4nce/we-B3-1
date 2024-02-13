import React, {useContext} from 'react';
import {Button, Card, Form} from "react-bootstrap";
import {Context} from "../../core/ContextWrapper";
import {useHistory} from "react-router-dom/cjs/react-router-dom";
import {Control} from "./FormGroups/Control";

export const Product = ({product, idx}) => {
    const {user, users, makeOrder} = useContext(Context);
    const nav = useHistory();

    const cardBody = (
        <Card.Body>
            <Card.Title>{product.title}</Card.Title>
            <Card.Text>{product.description}</Card.Text>
            {product.minOrderCount !== 0 && <Card.Text>Минимальное количество в заказе: {product.minOrderCount}</Card.Text>}
            {product.maxOrderCount !== 0 && <Card.Text>Максимальное количество в заказе: {product.maxOrderCount}</Card.Text>}
            <Card.Text>Регионы распространения: {product.regions.join(", ")}</Card.Text>
        </Card.Body>
    );

    const handler = (ev, executor) => {
        ev.preventDefault();
        const count = ev.target[0].value;
        const deliveryLimit = ev.target[1].value;
        const delivaryAddress = ev.target[2].value;
        makeOrder(idx, executor, count, deliveryLimit.getTime(), delivaryAddress)
    }

    const cardFooter = ({executor}) => (
        <Card.Footer>
            <Card.Text>В наличии: {users[executor].products[idx] ?? 0}шт.</Card.Text>
            <Form onSubmit={handler}>
                <Control
                    controlId={"count"}
                    type={"number"}
                    label={"Количество"}
                    min={product.minOrderCount === 0 ? 1 : product.minOrderCount}
                    max={product.maxOrderCount === 0 ? null : product.maxOrderCount}
                />
                <Control
                    controlId={"deliveryLimit"}
                    type={"date"}
                    label={"Желаемая дата доставки"}
                    min={new Date().toISOString().split("T")[0]}
                />
                <Control controlId={"deliveryAddress"} label={"Адрес доставки"} />
                <Button type={"submit"}>Заказать</Button>
            </Form>
        </Card.Footer>
    );

    return (
        <>
            {user.role === "CLIENT" ? product.distributors.map(distributor => (
                <Card>
                    <Card.Header>Дистрибутор: {distributor.login}</Card.Header>
                    {cardBody}
                    {cardFooter(distributor)}
                </Card>
            )) : (
                <Card>
                    <Card.Header>Производитель: {product.mader}</Card.Header>
                    {cardBody}
                    {cardFooter(product.mader)}
                </Card>
            )}
        </>
    );
};