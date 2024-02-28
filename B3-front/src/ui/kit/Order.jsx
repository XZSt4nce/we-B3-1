import React, {useContext} from 'react';
import {Button, Card, CardFooter, Form} from "react-bootstrap";
import {Context} from "../../core/ContextWrapper";
import {Product} from "./Product";
import {OrderStatus} from "../../constants/OrderStatus";
import {Control} from "./FormGroups/Control";
import {Errors} from "../../constants/Errors";

export const Order = ({order}) => {
    const {user, products, clarifyOrder, confirmOrCancelOrder, payOrder, completeOrder, takeOrder, actionExecuting} = useContext(Context);

    const handleClarify = async (ev) => {
        ev.preventDefault();
        const totalPrice = ev.target[0].value;
        const deliveryLimit = new Date(ev.target[1].value).toISOString().split("T")[0];
        const isPrepaymentAvailable = ev.target[2].checked;
        if (totalPrice < 1) {
            alert(Errors.INCORRECT_DATA);
        } else if (deliveryLimit < new Date().toISOString().split("T")[0]) {
            alert(Errors.INCORRECT_DATA);
        } else {
            await clarifyOrder(order.id, totalPrice, deliveryLimit, isPrepaymentAvailable);
        }
    };

    return (
        <Card>
            <Card.Header>SHA256: {order.hash}</Card.Header>
            <Card.Body>
                <Card.Title>Клиент: {order.clientKey}</Card.Title>
                <Card.Subtitle>Исполнитель: {order.executorKey}</Card.Subtitle>
                <Product isOrderProduct={true} product={products[order.productKey]} />
                <p>Количество: {order.amount}</p>
                {order.status !== "WAITING_FOR_EMPLOYEE" && <p>Стоимость: {order.price.toLocaleString()}</p>}
                <p>Дата создания заказа: {new Date(order.creationDate).toLocaleString()}</p>
                <p>Дата доставки: {order.deliveryDate}</p>
                <p>Адрес доставки: {order.deliveryAddress}</p>
                <p>Статус: {OrderStatus[order.status]}</p>
            </Card.Body>
            {!user.blocked && user.activated && (
                <>
                    {user.login === order.executorKey ? (
                        <>
                            {order.status === "WAITING_FOR_EMPLOYEE" ? (
                                <Card.Footer>
                                    <Form onSubmit={handleClarify}>
                                        <Control controlId={"total-price"} label={"Стоимость заказа"} type={"number"} min={1} />
                                        <Control
                                            controlId={"delivery-limit"}
                                            type={"date"}
                                            label={"Дата доставки"}
                                            min={new Date().toISOString().split("T")[0]}
                                            defaultValue={order.deliveryDate}
                                        />
                                        <Form.Check
                                            type={"checkbox"}
                                            label={"Предоплата возможна"}
                                        />
                                        <Button disabled={actionExecuting} type={"submit"}>Подтвердить</Button>
                                    </Form>
                                </Card.Footer>
                            ) : order.status === "EXECUTING" ? (
                                <Card.Footer>
                                    <Button disabled={actionExecuting} onClick={() => completeOrder(order.id)}>Завершить</Button>
                                </Card.Footer>
                            ) : order.status === "EXECUTING_PAID" && (
                                <Card.Footer>
                                    <Button disabled={actionExecuting} onClick={() => completeOrder(order.id)}>Завершить</Button>
                                </Card.Footer>
                            )}
                        </>
                    ) : user.login === order.clientKey && (
                        <>
                            {order.status === "WAITING_FOR_CLIENT" ? (
                                <CardFooter>
                                    <div className={"d-flex justify-content-between gap-3"}>
                                        <Button disabled={actionExecuting} onClick={() => confirmOrCancelOrder(order.id, false)}>Отклонить</Button>
                                        <Button disabled={actionExecuting} onClick={() => confirmOrCancelOrder(order.id, true)}>Принять</Button>
                                    </div>
                                </CardFooter>
                            ) : order.status === "WAITING_FOR_PAYMENT" ? (
                                <Card.Footer>
                                    <Button disabled={actionExecuting} onClick={() => payOrder(order.id)}>Оплатить</Button>
                                </Card.Footer>
                            ) : order.status === "WAITING_FOR_TAKING" && (
                                <Card.Footer>
                                    <Button disabled={actionExecuting} onClick={() => takeOrder(order.id)}>Забрать</Button>
                                </Card.Footer>
                            )}
                        </>
                    )}
                </>
            )}
        </Card>
    );
};