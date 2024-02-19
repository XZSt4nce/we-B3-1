import React, {useContext} from 'react';
import {Button, Card, Form} from "react-bootstrap";
import {Context} from "../../core/ContextWrapper";
import {Control} from "./FormGroups/Control";
import {Link} from "react-router-dom";
import {Errors} from "../../constants/Errors";

export const Product = ({product, amount, isOrderProduct=false, inStock=false}) => {
    const {user, users, makeOrder, confirmProduct} = useContext(Context);

    const cardBody = (
        <Card.Body>
            <Card.Title>{product.title}</Card.Title>
            <Card.Text>{product.description}</Card.Text>
            {(product.minOrderCount ?? 0) !== 0 && <Card.Text>Минимальное количество в заказе: {product.minOrderCount}</Card.Text>}
            {(product.maxOrderCount ?? 0) !== 0 && <Card.Text>Максимальное количество в заказе: {product.maxOrderCount}</Card.Text>}
            <Card.Text>Регионы распространения: {product.regions.join(", ")}</Card.Text>
            {!!amount && (<Card.Text>Количество: {amount}</Card.Text>)}
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
        if (minOrderCount > maxOrderCount) {
            alert(Errors.INCORRECT_DATA);
        } else {
            await confirmProduct(product.id, description, regions, minOrderCount, maxOrderCount, distributors);
        }
    };

    const cardFooter = ({executor}) =>
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
                                <Button type={"submit"}>Подтвердить</Button>
                            </Form>
                        </Card.Footer>
                    ) : !isOrderProduct && (
                        <Card.Footer>
                            <Card.Text>В наличии: {users[executor].productsProvided[product.id] ?? 0}шт.</Card.Text>
                            <Form onSubmit={handleMakeOrder}>
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
                    )}
                </>
        )

    return (amount > 0 || !inStock) && (
        <>
            {user.role === "CLIENT" ? product.distributors.map(distributor => (
                <Card>
                    <Link to={`/user/${distributor.login}`}>Дистрибутор: {distributor.login}</Link>
                    {cardBody}
                    {cardFooter(distributor)}
                </Card>
            )) : (
                <Card>
                    <Link to={`/user/${product.mader}`}>Производитель: {product.mader}</Link>
                    {cardBody}
                    {cardFooter(product.mader)}
                </Card>
            )}
        </>
    );
};