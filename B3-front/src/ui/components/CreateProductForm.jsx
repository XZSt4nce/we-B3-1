import React, {useContext} from 'react';
import {Button, Form} from "react-bootstrap";
import {Context} from "../../core/ContextWrapper";
import {Control} from "../kit/FormGroups/Control";
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {Errors} from "../../constants/Errors";

export const CreateProductForm = () => {
    const {createProduct, actionExecuting} = useContext(Context);

    const handler = async (ev) => {
        ev.preventDefault();
        const title = ev.target[0].value;
        const description = ev.target[1].value;
        const regions = ev.target[2].value.split(",").map(region => region.trim().toUpperCase()).filter(region => region !== "");
        if (regions.length < 1) {
            alert(Errors.INCORRECT_DATA);
        } else {
            await createProduct(title, description, regions);
        }
    };

    return (
        <WhiteBlock title={"Создать продукт"}>
            <Form onSubmit={handler}>
                <Control controlId={"title"} label={"Название"} />
                <Control controlId={"description"} label={"Описание"} />
                <Control controlId={"regions"} label={"Регионы"} placeholder={"Введите через запятую регионы"} />
                <Button variant={"success"} disabled={actionExecuting} type={"submit"}>Создать</Button>
            </Form>
        </WhiteBlock>
    );
};