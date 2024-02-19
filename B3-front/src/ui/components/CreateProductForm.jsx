import React, {useContext} from 'react';
import {Button, Form} from "react-bootstrap";
import {Context} from "../../core/ContextWrapper";
import {Control} from "../kit/FormGroups/Control";

export const CreateProductForm = () => {
    const {createProduct} = useContext(Context);

    const handler = async (ev) => {
        ev.preventDefault();
        const title = ev.target[0].value;
        const description = ev.target[1].value;
        const regions = ev.target[2].value.split(",").map(region => region.trim());
        await createProduct(title, description, regions);
    };

    return (
        <Form onSubmit={handler}>
            <h2 className={"text-center"}>Добавить продукт</h2>
            <Control controlId={"title"} label={"Название"} />
            <Control controlId={"description"} label={"Описание"} />
            <Control controlId={"regions"} label={"Регионы"} placeholder={"Введите через запятую регионы"} />
            <Button type={"submit"}>Создать</Button>
        </Form>
    );
};