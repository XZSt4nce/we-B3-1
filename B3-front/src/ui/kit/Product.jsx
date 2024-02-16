import React, {useContext} from 'react';
import {Button, Card, Form} from "react-bootstrap";
import {Context} from "../../core/ContextWrapper";
import {useHistory} from "react-router-dom/cjs/react-router-dom";
import {Control} from "./FormGroups/Control";
import {Link} from "react-router-dom";
import {ContractKeys} from "../../constants/ContractKeys";

export const Product = ({product, amount, isOrderProduct=false}) => {
    const {user, users, makeOrder} = useContext(Context);
    const nav = useHistory();

    const cardBody = (
        <Card.Body>
            <Card.Title>{product.title}</Card.Title>
            <Card.Text>{product.description}</Card.Text>
            {product.minOrderCount !== 0 && <Card.Text>Минимальное количество в заказе: {product.minOrderCount}</Card.Text>}
            {product.maxOrderCount !== 0 && <Card.Text>Максимальное количество в заказе: {product.maxOrderCount}</Card.Text>}
            <Card.Text>Регионы распространения: {product.regions.join(", ")}</Card.Text>
            {!!amount && (<Card.Text>Количество: {amount}</Card.Text>)}
        </Card.Body>
    );

    const handler = (ev, executor) => {
        ev.preventDefault();
        const count = ev.target[0].value;
        const deliveryLimit = ev.target[1].value;
        const deliveryAddress = ev.target[2].value;
        makeOrder(product.id, executor, count, deliveryLimit.getTime(), deliveryAddress)
    }

    const cardFooter = ({executor}) =>
        ((user.role === "CLIENT" && (users[executor].role === "DISTRIBUTOR" || users[executor].role === "OPERATOR")) ||
        ((user.role === "DISTRIBUTOR" || user.role === "OPERATOR") && users[executor].role === "SUPPLIER")) &&
        !isOrderProduct && (
        <Card.Footer>
            <Card.Text>В наличии: {users[executor].productsProvided[product.id] ?? 0}шт.</Card.Text>
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
                    <Link to={`${ContractKeys.USERS_MAPPING_PREFIX}_${distributor.login}`}>Дистрибутор: {distributor.login}</Link>
                    {cardBody}
                    {cardFooter(distributor)}
                </Card>
            )) : (
                <Card>
                    <Link to={`${ContractKeys.USERS_MAPPING_PREFIX}_${product.mader}`}>Производитель: {product.mader}</Link>
                    {cardBody}
                    {cardFooter(product.mader)}
                </Card>
            )}
        </>
    );
};