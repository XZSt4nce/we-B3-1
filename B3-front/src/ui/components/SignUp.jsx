import React, {useContext, useState} from 'react';
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
    const [organizationKey, setOrganizationKey] = useState("");
    const {users, signUp} = useContext(Context);

    const clearData = () => {
        setTitle("");
        setDescription("");
        setOrganizationKey("");
    };

    const handler = async (ev) => {
        ev.preventDefault();
        const login = ev.target[0].value;
        const password = ev.target[1].value;
        const fullName = ev.target[2].value;
        const email = ev.target[3].value;
        const regions = JSON.stringify(ev.target[5].value.split(",").map(el => el.trim()));
        if (organizationKey) {
            if (organizationKey < 0) {
                alert(Errors.INCORRECT_DATA);
            } else {
                if (users[organizationKey]) {
                    if (users[organizationKey].role === role) {
                        signUp(login, password, title, description, fullName, email, regions, organizationKey);
                    } else {
                        alert(Errors.NO_MATCH_ORGANIZATION_ROLE);
                    }
                } else {
                    alert(Errors.ORGANIZATION_NOT_FOUND);
                }
            }
        } else {
            await signUp(login, password, title, description, fullName, email, regions, -1)
                .then(data => {
                    if (data) {
                        console.log(data);
                    }
                });
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
                        onChange={e => {
                            clearData();
                            setRole(e.target.value);
                        }}
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
                        <Control controlId={"regions"} label={"Регионы"} placeholder={"Введите регион(-ы), в которых Вы будете распространять товары"} />
                        <Form.Check
                            type={"checkbox"}
                            id={"isEmployee"}
                            label={"Я сотрудник"}
                            onChange={e => {
                                clearData();
                                setIsEmployee(e.target.checked);
                            }}
                        />
                        {isEmployee ? (
                            <Control
                                controlId={"organization"}
                                type={"number"}
                                label={"Публичный ключ организации"}
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
                <Button type={"submit"}>Зарегистрироваться</Button>
            </Form>
        </WhiteBlock>
    );
};