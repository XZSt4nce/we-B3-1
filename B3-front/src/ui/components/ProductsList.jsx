import React from 'react';
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {Product} from "../kit/Product";
import {EmptyListPlug} from "./EmptyListPlug";

export const ProductsList = ({products, amounts, title="Ассортимент товаров"}) => {
    return (
        <WhiteBlock title={title}>
            {products.length === 0 ? <EmptyListPlug /> : products.map((product, idx) => (
                <Product product={product} amount={amounts[product.id]} key={idx} />
            ))}
        </WhiteBlock>
    );
};