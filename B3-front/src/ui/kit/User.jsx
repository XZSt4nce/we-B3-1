import React, {useContext} from 'react';
import {Button, Card, Form} from "react-bootstrap";
import {Context} from "../../core/ContextWrapper";
import {Control} from "./FormGroups/Control";
import {UserRole} from "../../constants/UserRole";
import {ProductsList} from "../components/ProductsList";
import {Errors} from "../../constants/Errors";

export const User = ({userStruct}) => {
    const {user, activateUser, blockUser, organizations, products, actionExecuting} = useContext(Context);

    const handleActivate = async (ev) => {
        ev.preventDefault();
        const fullName = ev.target[0].value;
        const email = ev.target[1].value;
        const regions = ev.target[2].value.split(",").map(region => region.trim()).filter(region => region !== "");
        if (regions.length === 0) {
            alert(Errors.INCORRECT_DATA);
        } else {
            await activateUser(userStruct.login, fullName, email, regions);
        }
    };

    return userStruct.activated ? (
        <Card>
            <Card.Body>
                <Card.Title>Логин: {userStruct.login}</Card.Title>
                {userStruct.role === "CLIENT" ? (
                    <Card.Text>Регион: {userStruct.regions}</Card.Text>
                ) : (
                    <>
                        <Card.Text>Название организации: {organizations[userStruct.organizationKey].title}</Card.Text>
                        {userStruct.role === "SUPPLIER" && <Card.Text>Описание организации: {organizations[userStruct.organizationKey].description}</Card.Text>}
                        <Card.Text>Регионы распространения: {userStruct.regions.join(", ")}</Card.Text>
                    </>
                )}
                <Card.Text>ФИО: {userStruct.fullName}</Card.Text>
                <Card.Text>E-mail: {userStruct.email}</Card.Text>
                <Card.Text>Роль: {UserRole[userStruct.role]}</Card.Text>
                {user.role === "OPERATOR" ? (
                    <>
                        <Card.Text>Баланс: {userStruct.balance.toLocaleString()}</Card.Text>
                        {!userStruct.blocked && (
                            <Button className={"w-100"} disabled={actionExecuting} variant={"danger"} onClick={() => blockUser(userStruct.login)}>Заблокировать</Button>
                        )}
                        {userStruct.role !== "CLIENT" && (
                            <ProductsList
                                title={"Ассортимент"}
                                products={userStruct.productsProvided.map(product => products[product])}
                            />
                        )}
                        {userStruct.role !== "SUPPLIER" && (
                            <ProductsList
                                title={"Склад"}
                                products={Object.keys(userStruct.products).map(product => products[product])}
                                amounts={userStruct.products}
                            />
                        )}
                    </>
                ) : (
                    <ProductsList
                        title={"Ассортимент"}
                        products={userStruct.productsProvided
                            .map(product => products[product])
                            .filter(product => product.confirmed)}
                    />
                )}
            </Card.Body>
        </Card>
    ) : user.role === "OPERATOR" ? (
        <Card>
            <Card.Body>
                <Card.Title>Логин: {userStruct.login}</Card.Title>
                <Form onSubmit={handleActivate}>
                    <Control controlId={"fullName"} label={"ФИО"} defaultValue={userStruct.fullName} />
                    <Control controlId={"email"} label={"E-mail"} type={"email"} defaultValue={userStruct.email} />
                    {userStruct.role === "CLIENT" ? (
                        <Control controlId={"regions"} label={"Регион"} defaultValue={userStruct.regions} />
                    ) : (
                        <Control controlId={"regions"} label={"Регион(-ы) распространения"} defaultValue={userStruct.regions.join(", ")} />
                    )}
                    <Button className={"w-100"} disabled={actionExecuting} variant={"success"} type={"submit"}>Активировать</Button>
                </Form>
            </Card.Body>
        </Card>
    ) : (
        <p>404. Страница не найдена</p>
    );
};