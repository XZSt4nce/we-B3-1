import React, {useContext, useEffect, useState} from 'react';
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {Control} from "../kit/FormGroups/Control";
import {Button, Form} from "react-bootstrap";
import {Context} from "../../core/ContextWrapper";
import {Errors} from "../../constants/Errors";

export const SignUp = () => {
    const [role, setRole] = useState("CLIENT");
    const [isEmployee, setIsEmployee] = useState(false);
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [organizationKey, setOrganizationKey] = useState(null);
    const {organizations, signUp, actionExecuting} = useContext(Context);

    useEffect(() => {
        setTitle("");
        setDescription("");
        setOrganizationKey(null);
    }, [isEmployee, role]);

    const handler = async (ev) => {
        ev.preventDefault();
        const login = ev.target[0].value;
        const password = ev.target[1].value;
        const fullName = ev.target[2].value;
        const email = ev.target[3].value;
        const regions = ev.target[5].value.split(",").map(region => region.trim()).filter(region => region !== "");

        if (regions.length < 1) {
            alert(Errors.INCORRECT_DATA);
        } else if (role === "CLIENT" && regions.length !== 1) {
            alert(Errors.INCORRECT_DATA);
        }

        if (organizationKey) {
            if (organizationKey < 0) {
                alert(Errors.INCORRECT_DATA);
            } else {
                if (organizations[organizationKey]) {
                    if (organizations[organizationKey].role === role) {
                        signUp(login, password, title, description, fullName, email, regions, organizationKey);
                    } else {
                        alert(Errors.NO_MATCH_ORGANIZATION_ROLE);
                    }
                } else {
                    alert(Errors.ORGANIZATION_NOT_FOUND);
                }
            }
        } else {
            await signUp(login, password, title, description, fullName, email, regions, -1);
        }
    }

    return (
        <WhiteBlock title={"Регистрация в системе"}>
            <Form onSubmit={handler}>
                <Control controlId={"signUpLogin"} label={"Логин"} />
                <Control controlId={"signUpPassword"} label={"Пароль"} type={"password"} />
                <Control controlId={"fullName"} label={"ФИО"} />
                <Control type={"email"} controlId={"signUpEmail"} label={"E-mail"} />
                <Form.Group className='mb-3' controlId='role'>
                    <Form.Label>Ваша роль*</Form.Label>
                    <Form.Select
                        name='role'
                        defaultValue={role}
                        onChange={e => setRole(e.target.value)}
                    >
                        <option value='CLIENT'>Пользователь</option>
                        <option value='DISTRIBUTOR'>Дистрибутор</option>
                        <option value='SUPPLIER'>Поставщик</option>
                    </Form.Select>
                </Form.Group>
                {role === "CLIENT" ? (
                    <Control controlId={"region"} label={"Регион"} placeholder={"Введите Ваш регион"} />
                ) : (
                    <>
                        <Control controlId={"regions"} label={"Регионы"} placeholder={"Через запятую введите регион(-ы), в которых Вы будете распространять товары"} />
                        <Form.Check
                            type={"checkbox"}
                            id={"isEmployee"}
                            label={"Я сотрудник"}
                            onChange={e => setIsEmployee(e.target.checked)}
                        />
                        {isEmployee ? (
                            <Control
                                controlId={"organization"}
                                type={"number"}
                                label={"Ключ организации"}
                                min={0}
                                max={organizations.length - 1}
                                placeholder={"Введите, если Вы сотрудник организации"}
                                required={false}
                                onChange={e => setOrganizationKey(e.target.value)}
                            />
                        ) : (
                            <>
                                <Control controlId={"title"} label={"Название"} onChange={e => setTitle(e.target.value)} />
                                {role === "SUPPLIER" && (
                                    <Control controlId={"description"} label={"Описание"} onChange={e => setDescription(e.target.value)} />
                                )}
                            </>
                        )}
                    </>
                )}
                <Button disabled={actionExecuting} type={"submit"}>Зарегистрироваться</Button>
            </Form>
        </WhiteBlock>
    );
};