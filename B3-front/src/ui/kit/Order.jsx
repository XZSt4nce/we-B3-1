import React, {useContext} from 'react';
import {Button, Card, CardFooter, Form} from "react-bootstrap";
import {Context} from "../../core/ContextWrapper";
import {Product} from "./Product";
import {OrderStatus} from "../../constants/OrderStatus";
import {Control} from "./FormGroups/Control";

export const Order = ({order}) => {
    const {user, products, clarifyOrder, confirmOrCancelOrder, payOrder, completeOrder, takeOrder, actionExecuting} = useContext(Context);

    const handleClarify = async (ev) => {
        ev.preventDefault();
        const totalPrice = ev.target[0].value;
        const deliveryLimit = ev.target[1].value;
        const isPrepaymentAvailable = ev.target[2].checked;
        await clarifyOrder(order.id, totalPrice, deliveryLimit, isPrepaymentAvailable);
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
            {!user.blocked && user.activated (
                <>
                    {user.login === order.executorKey ? (
                        <>
                            {order.status === "WAITING_FOR_EMPLOYEE" ? (
                                <Card.Footer>
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
                            ) : order.status === "EXECUTING" ? (
                                <Card.Footer>
                                    <Button disabled={actionExecuting} onClick={completeOrder}>Завершить</Button>
                                </Card.Footer>
                            ) : order.status === "EXECUTING_PAID" && (
                                <Card.Footer>
                                    <Button disabled={actionExecuting} onClick={completeOrder}>Завершить</Button>
                                </Card.Footer>
                            )}
                        </>
                    ) : user.login === order.clientKey && (
                        <>
                            {order.status === "WAITING_FOR_CLIENT" ? (
                                <CardFooter>
                                    <div className={"d-flex justify-content-between"}>
                                        <Button disabled={actionExecuting} onClick={() => confirmOrCancelOrder(false)}>Отклонить</Button>
                                        <Button disabled={actionExecuting} onClick={() => confirmOrCancelOrder(true)}>Принять</Button>
                                    </div>
                                </CardFooter>
                            ) : order.status === "WAITING_FOR_PAYMENT" ? (
                                <Card.Footer>
                                    <Button disabled={actionExecuting} onClick={payOrder}>Оплатить</Button>
                                </Card.Footer>
                            ) : order.status === "WAITING_FOR_TAKING" && (
                                <Card.Footer>
                                    <Button disabled={actionExecuting} onClick={takeOrder}>Забрать</Button>
                                </Card.Footer>
                            )}
                        </>
                    )}
                </>
            )}
        </Card>
    );
};