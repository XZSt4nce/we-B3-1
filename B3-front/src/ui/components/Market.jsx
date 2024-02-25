import React, {useContext, useEffect, useState} from 'react';
import {ProductsList} from "./ProductsList";
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {Context} from "../../core/ContextWrapper";
import {Form} from "react-bootstrap";
import {Control} from "../kit/FormGroups/Control";

export const Market = () => {
    const {products} = useContext(Context);
    const [inStock, setInStock] = useState(false);
    const [minOrderCount, setMinOrderCount] = useState(1)
    const [maxOrderCount, setMaxOrderCount] = useState(0)

    useEffect(() => {
        if (minOrderCount < 1) {
            setMinOrderCount(1);
        }
        if (minOrderCount > maxOrderCount) {
            setMinOrderCount(maxOrderCount);
        }
    }, [minOrderCount]);

    useEffect(() => {
        if (maxOrderCount < 0) {
            setMaxOrderCount(0);
        }
        if (maxOrderCount < minOrderCount) {
            setMaxOrderCount(minOrderCount);
        }
    }, [maxOrderCount]);

    return (
        <WhiteBlock title={"Торговая площадка"}>
            <Form className={"border rounded p-2"}>
                <h2 className={"text-center"}>Фильтр</h2>
                <Form.Check label={"В наличии"} type={"checkbox"} onChange={(ev) => setInStock(ev.target.checked)} />
                <Control
                    controlId={"minOrderCount"}
                    label={"Минимальное количество за заказ"}
                    type={"number"}
                    min={1}
                    onChange={(ev) => setMinOrderCount(ev.target.value)}
                />
                <Control
                    controlId={"maxOrderCount"}
                    label={"Максимальное количество за заказ"}
                    type={"number"}
                    min={0}
                    onChange={(ev) => setMaxOrderCount(ev.target.value)}
                />
            </Form>
            <ProductsList
                title={"Продукты"}
                inStock={inStock}
                products={products
                    .filter(product =>
                        product.confirmed
                        && product.minOrderCount >= minOrderCount
                        && (maxOrderCount === 0 || product.maxOrderCount <= maxOrderCount)
                    )} />
        </WhiteBlock>
    );
};