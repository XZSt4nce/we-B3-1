import React, {useContext, useEffect, useState} from 'react';
import {ProductsList} from "./ProductsList";
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {Context} from "../../core/ContextWrapper";
import {Form} from "react-bootstrap";
import {Control} from "../kit/FormGroups/Control";

export const Market = () => {
    const {user, products} = useContext(Context);
    const [inStock, setInStock] = useState(false);
    const [minOrderCount, setMinOrderCount] = useState(1)
    const [maxOrderCount, setMaxOrderCount] = useState(100)

    useEffect(() => {
        if (minOrderCount > maxOrderCount) {
            setMaxOrderCount(minOrderCount);
        }
    }, [minOrderCount]);

    useEffect(() => {
        if (maxOrderCount < minOrderCount) {
            setMinOrderCount(maxOrderCount);
        }
    }, [maxOrderCount]);

    return (
        <WhiteBlock title={"Торговая площадка"}>
            <Form className={"border rounded p-2"}>
                <h2 className={"text-center"}>Фильтр</h2>
                {user.role === "CLIENT" && (
                    <Form.Check
                        label={"В наличии"}
                        type={"checkbox"}
                        onChange={(ev) => setInStock(ev.target.checked)} />
                )}
                <Control
                    controlId={"minOrderCount"}
                    label={"Минимальное количество за заказ"}
                    type={"number"}
                    min={1}
                    value={minOrderCount}
                    onChange={(ev) => setMinOrderCount(+ev.target.value)}
                />
                <Control
                    controlId={"maxOrderCount"}
                    label={"Максимальное количество за заказ"}
                    type={"number"}
                    min={1}
                    value={maxOrderCount}
                    onChange={(ev) => setMaxOrderCount(+ev.target.value)}
                />
            </Form>
            <ProductsList
                title={"Продукты"}
                inStock={inStock}
                products={products
                    .filter(product =>
                        product.confirmed
                        && product.minOrderCount >= minOrderCount
                        && product.maxOrderCount <= maxOrderCount
                    )} />
        </WhiteBlock>
    );
};