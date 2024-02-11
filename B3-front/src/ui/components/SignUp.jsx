import React, {useContext, useState} from 'react';
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {Control} from "../kit/FormGroups/Control";
import {Button, Form} from "react-bootstrap";
import {Context} from "../../core/ContextWrapper";

export const SignUp = () => {
    const [role, setRole] = useState("CLIENT");
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [organizationKey, setOrganizationKey] = useState("");
    const {users, signUp} = useContext(Context);

    const onRoleChange = (ev) => {
        setTitle("");
        setDescription("");
        setOrganizationKey("");
        setRole(ev.target.value);
    }

    const handler = (ev) => {
        ev.preventDefault();
        const login = ev.target[0];
        const password = ev.target[1];
        const fullName = ev.target[2];
        const email = ev.target[3];
        const regions = ev.target[4].split(",").map(el => el.trim());
        if (organizationKey) {
            if (users[organizationKey]) {
                if (users[organizationKey].role === role) {
                    signUp(login, password, title, description, fullName, email, regions, organizationKey);
                } else {
                    alert("Чтобы стать сотрудником организации, нужно выбрать такую же роль, как у организации");
                }
            } else {
                alert("Организации с данным ключом не существует");
            }
        } else {
            signUp(login, password, title, description, fullName, email, regions, organizationKey);
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
                        defaultValue={"CLIENT"}
                        onChange={onRoleChange}
                    >
                        <option value='CLIENT'>Пользователь</option>
                        <option value='DISTRIBUTOR'>Дистрибутор</option>
                        <option value='SUPPLIER'>Поставщик</option>
                    </Form.Select>
                </Form.Group>
                {role === "CLIENT" ? (
                    <Control controlId={"region"} label={"Регион"} />
                ) : (
                    <>
                        <Control controlId={"title"} label={"Название"} onChange={e => setTitle(e.target.value)} />
                        {role === "SUPPLIER" && (
                            <Control controlId={"description"} label={"Описание"} onChange={e => setDescription(e.target.value)} />
                        )}
                        <Control controlId={"regions"} label={"Регионы"} />
                        <Control
                            controlId={"organization"}
                            label={"Публичный ключ организации"}
                            placeholder={"Введите, если Вы сотрудник организации"}
                            required={false}
                            onChange={e => setOrganizationKey(e.target.value)}
                        />
                    </>
                )}
                <Button type={"submit"}>Зарегистрироваться</Button>
            </Form>
        </WhiteBlock>
    );
};