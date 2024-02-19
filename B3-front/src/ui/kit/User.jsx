import React, {useContext} from 'react';
import {Button, Card, Form} from "react-bootstrap";
import {Context} from "../../core/ContextWrapper";
import {Control} from "./FormGroups/Control";
import {UserRole} from "../../constants/UserRole";
import {ProductsList} from "../components/ProductsList";

export const User = ({userStruct}) => {
    const {user, activateUser, blockUser, organizations, products} = useContext(Context);

    const handleActivate = async (ev) => {
        ev.preventDefault();
        const description = ev.target[0].value;
        const fullName = ev.target[1].value;
        const email = ev.target[2].value;
        const regions = JSON.stringify(ev.target[3].value.split(",").map(region => region.trim()));
        await activateUser(userStruct.login, description, fullName, email, regions);
    };

    return (
        <Card>
            <Card.Body>
                <Card.Title>Логин: {userStruct.login}</Card.Title>
                {user.role === "OPERATOR" ? (
                    <>
                        {user.activated ? (
                            <>
                                <Card.Text>Баланс: {userStruct.balance.toLocaleString()}</Card.Text>
                                <Card.Text>Название организации: {organizations[userStruct.organizationKey].title}</Card.Text>
                                <Card.Text>Описание организации: {organizations[userStruct.organizationKey].description}</Card.Text>
                                <Card.Text>Регионы распространения: {userStruct.regions}</Card.Text>
                                <Card.Text>ФИО: {userStruct.fullName}</Card.Text>
                                <Card.Text>E-mail: {userStruct.email}</Card.Text>
                                <Card.Text>Роль: {UserRole[userStruct.role]}</Card.Text>
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
                                <Button variant={"danger"} onClick={() => blockUser(userStruct.login)}>Заблокировать</Button>
                            </>
                        ) : (
                            <Form onSubmit={handleActivate}>
                                <Control controlId={"description"} label={"Описание"} defaultValue={userStruct.description} />
                                <Control controlId={"fullName"} label={"Описание"} defaultValue={userStruct.fullName} />
                                <Control controlId={"email"} label={"Описание"} type={"email"} defaultValue={userStruct.email} />
                                <Control controlId={"regions"} label={"Описание"} defaultValue={userStruct.regions.join(", ")} />
                                <Button variant={"success"} type={"submit"}>Активировать</Button>
                            </Form>
                        )}
                    </>
                ) : (
                    <>
                        <Card.Text>Название организации: {organizations[userStruct.organizationKey].title}</Card.Text>
                        <Card.Text>Описание организации: {organizations[userStruct.organizationKey].description}</Card.Text>
                        <Card.Text>Регионы распространения: {userStruct.regions}</Card.Text>
                        <Card.Text>ФИО: {userStruct.fullName}</Card.Text>
                        <Card.Text>E-mail: {userStruct.email}</Card.Text>
                        <Card.Text>Роль: {UserRole[userStruct.role]}</Card.Text>
                        <ProductsList
                            title={"Ассортимент"}
                            products={userStruct.productsProvided
                                .map(product => products[product])
                                .filter(product => product.confirmed)}
                        />
                    </>
                )}
            </Card.Body>
        </Card>
    );
};