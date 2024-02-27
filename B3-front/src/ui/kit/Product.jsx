import React, {useContext} from 'react';
import {Button, Card, Form} from "react-bootstrap";
import {Context} from "../../core/ContextWrapper";
import {Control} from "./FormGroups/Control";
import {Link} from "react-router-dom";

export const Product = ({product, amount, isOrderProduct=false, inStock=false}) => {
    const {user, users, makeOrder, confirmProduct, actionExecuting} = useContext(Context);

    const cardBody = (
        <Card.Body>
            <Card.Title>{product.title}</Card.Title>
            <Card.Text>{product.description}</Card.Text>
            {product.minOrderCount > 0 && <Card.Text>Минимальное количество в заказе: {product.minOrderCount}</Card.Text>}
            {product.maxOrderCount > 0 && <Card.Text>Максимальное количество в заказе: {product.maxOrderCount}</Card.Text>}
            <Card.Text>Регионы распространения: {product.regions.join(", ")}</Card.Text>
            {!!amount && <Card.Text>Количество: {amount}</Card.Text>}
            {!product.confirmed && <Card.Text className={"fw-bold"}>Не подтверждён</Card.Text>}
        </Card.Body>
    );

    const handleMakeOrder = async (ev, executor) => {
        ev.preventDefault();
        const count = ev.target[0].value;
        const deliveryLimit = ev.target[1].value;
        const deliveryAddress = ev.target[2].value;
        await makeOrder(product.id, executor, count, deliveryLimit.getTime(), deliveryAddress);
    };

    const handleConfirmProduct = async (ev) => {
        ev.preventDefault();
        const description = ev.target[0].value;
        const regions = ev.target[1].value.split(",").map(region => region.trim());
        const minOrderCount = ev.target[2].value;
        const maxOrderCount = ev.target[3].value;
        const distributors = ev.target[4].value.split(",").map(region => region.trim());
        await confirmProduct(product.id, description, regions, minOrderCount, maxOrderCount, distributors);
    };

    const cardFooter = (executor) =>
        ((user.role === "CLIENT" && (users[executor].role === "DISTRIBUTOR" || users[executor].role === "OPERATOR"))
            || ((user.role === "DISTRIBUTOR" || user.role === "OPERATOR") && users[executor].role === "SUPPLIER")) && (
            <>
                {!product.confirmed ? (
                    <Card.Footer>
                        <Form onSubmit={handleConfirmProduct}>
                            <Control controlId={"description"} label={"Описание"} defaultValue={product.description} />
                            <Control controlId={"regions"} label={"Регионы"} placeholder={"Введите через запятую регионы"} defaultValue={product.regions.join(", ")} />
                            <Control controlId={"minOrderCount"} min={0} type={"number"} label={"Минимальное количество за заказ"} />
                            <Control controlId={"maxOrderCount"} min={0} type={"number"} label={"Максимальное количество за заказ"} />
                            <Control controlId={"distributors"} label={"Дистрибуторы"} placeholder={"Введите через запятую дистрибуторов"} />
                            <Button disabled={actionExecuting} type={"submit"}>Подтвердить</Button>
                        </Form>
                    </Card.Footer>
                ) : !isOrderProduct && (
                    <Card.Footer>
                        <Card.Text>В наличии: {users[executor].productsProvided[product.id] ?? 0} шт.</Card.Text>
                        {(product.minOrderCount <= users[executor].productsProvided[product.id] ?? 0) && (
                            <Form onSubmit={(ev) => handleMakeOrder(ev, executor)}>
                                <Control
                                    controlId={"count"}
                                    type={"number"}
                                    label={"Количество"}
                                    min={product.minOrderCount}
                                    max={product.maxOrderCount === 0 ? null : product.maxOrderCount}
                                />
                                <Control
                                    controlId={"deliveryLimit"}
                                    type={"date"}
                                    label={"Желаемая дата доставки"}
                                    min={new Date().toISOString().split("T")[0]}
                                />
                                <Control controlId={"deliveryAddress"} label={"Адрес доставки"} />
                                <Button disabled={actionExecuting} type={"submit"}>Заказать</Button>
                            </Form>
                        )}
                    </Card.Footer>
                )}
            </>
        );

    return user.role === "CLIENT" ? (
        <>
            {(amount > 0 || !inStock) && product.distributors.map((distributor, idx) => (
                <Card className={"p-2"} key={idx}>
                    <Link to={`/user/${distributor}`}>Дистрибутор: {distributor}</Link>
                    {cardBody}
                    {cardFooter(distributor)}
                </Card>
            ))}
        </>
    ) : (
        <Card className={"p-2"}>
            <Link to={`/user/${product.mader}`}>Производитель: {product.mader}</Link>
            {cardBody}
            {cardFooter(product.mader)}
        </Card>
    );
};