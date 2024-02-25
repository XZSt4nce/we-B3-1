import React, {useContext} from 'react';
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {Button, Form} from "react-bootstrap";
import {Control} from "../kit/FormGroups/Control";
import {Context} from "../../core/ContextWrapper";

export const SignIn = () => {
    const {signIn, actionExecuting} = useContext(Context);

    const handler = (ev) => {
        ev.preventDefault();
        const login = ev.target[0].value;
        const password = ev.target[1].value;
        signIn(login, password);
    }

    return (
        <WhiteBlock title={"Вход"}>
            <Form onSubmit={handler}>
                <Control controlId={"signInLogin"} label={"Логин"} />
                <Control controlId={"signInPassword"} label={"Пароль"} type={"password"} />
                <Button disabled={actionExecuting} type={"submit"}>Войти</Button>
            </Form>
        </WhiteBlock>
    );
};