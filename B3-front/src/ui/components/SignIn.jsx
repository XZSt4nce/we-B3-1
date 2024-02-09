import React, {useContext} from 'react';
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {Button, Form} from "react-bootstrap";
import {Control} from "../kit/FormGroups/Control";
import {Context} from "../../core/ContextWrapper";

export const SignIn = () => {
    const {signIn} = useContext(Context);

    const handler = (ev) => {
        ev.preventDefault();
        signIn(ev.target[0].value);
    }

    return (
        <WhiteBlock title={"Вход"}>
            <Form onSubmit={handler}>
                <Control controlId={"login"} label={"Логин"} />
                <Button type={"submit"}>Войти</Button>
            </Form>
        </WhiteBlock>
    );
};