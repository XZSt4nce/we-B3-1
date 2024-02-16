import React, {useContext} from 'react';
import {Button, Card, CardFooter, Form} from "react-bootstrap";
import {Context} from "../../core/ContextWrapper";
import {Product} from "./Product";
import {OrderStatus} from "../../constants/OrderStatus";
import {Control} from "./FormGroups/Control";

export const Order = ({order}) => {
    const {user, products, clarifyOrder, confirmOrCancelOrder, payOrder, completeOrder, takeOrder} = useContext(Context);

    const isClient = () => {
        return user.login === order.clientKey;
    };

    const isExecutor = () => {
        return user.login === order.executorKey;
    };

    const handleClarify = (ev) => {
        ev.preventDefault();
        const totalPrice = ev.target[0].value;
        const deliveryLimit = ev.target[1].value;
        const isPrepaymentAvailable = ev.target[2].checked;
        clarifyOrder(order.id, totalPrice, deliveryLimit, isPrepaymentAvailable);
    };
    const handleConfirm = () => {
        confirmOrCancelOrder(true);
    };

    const handleCancel = () => {
        confirmOrCancelOrder(false);
    };

    const handlePayOrder = (ev) => {
        ev.preventDefault();
        payOrder();
    };

    const handleCompleteOrder = (ev) => {
        ev.preventDefault();
        completeOrder();
    };

    const handleTakeOrder = (ev) => {
        ev.preventDefault();
        takeOrder();
    };

    return (
        <Card>
            <Card.Header>SHA256: {order.hash}</Card.Header>
            <Card.Body>
                <Card.Title>Клиент: {order.clientKey}</Card.Title>
                <Card.Subtitle>Исполнитель: {order.executorKey}</Card.Subtitle>
                <Product isOrderProduct={true} product={products[order.productKey]} />
                <p>Количество: {order.amount}</p>
                <p>Стоимость: {order.price.toLocaleString()}</p>
                <p>Дата создания заказа: {new Date(order.creationDate)}</p>
                <p>Дата доставки: {new Date(order.deliveryDate)}</p>
                <p>Адрес доставки: {order.deliveryAddress}</p>
                <p>Статус: {OrderStatus[order.status]}</p>
            </Card.Body>
            {order.status === "WAITING_FOR_EMPLOYEE" && isExecutor() ? (
                <Card.Footer>
                    <h2 className={"text-center"}>Уточнение заказа</h2>
                    <Form onSubmit={handleClarify}>
                        <Control controlId={"total-price"} label={"Стоимость заказа"} type={"number"} min={1} />
                        <Control
                            controlId={"delivery-limit"}
                            label={"Дата доставки"}
                            min={new Date().toISOString().split("T")[0]}
                            defaultValue={new Date(order.deliveryLimit).toISOString().split("T")[0]}
                        />
                        <Form.Check
                            type={"checkbox"}
                            label={"Предоплата возможна"}
                        />
                    </Form>
                </Card.Footer>
            ) : order.status === "WAITING_FOR_CLIENT" && isClient() ? (
                <CardFooter>
                    <h2 className={"text-center"}>Принять/отклонить заказ</h2>
                    <div className={"d-flex justify-content-between"}>
                        <Button da>Отклонить</Button>
                    </div>
                </CardFooter>
            )}
        </Card>
    );
};