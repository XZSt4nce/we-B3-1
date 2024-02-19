import React from 'react';
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {Product} from "../kit/Product";
import {EmptyListPlug} from "./EmptyListPlug";

export const ProductsList = ({products, title, inStock=false}) => {
    return (
        <WhiteBlock title={title}>
            {products.length === 0 ? <EmptyListPlug /> : products.map((product, idx) =>
                <Product product={product} idx={idx} key={idx} inStock={inStock} />
            )}
        </WhiteBlock>
    );
};