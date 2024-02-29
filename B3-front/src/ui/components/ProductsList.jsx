import React from 'react';
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {Product} from "../kit/Product";
import {EmptyListPlug} from "./EmptyListPlug";

export const ProductsList = ({products, title, ownerStruct, inStock=false, isInPossession=false}) => {
    return (
        <WhiteBlock title={title} className={"d-flex flex-column gap-3"}>
            {products.length === 0 ? <EmptyListPlug /> : products.map((product, idx) =>
                <Product product={product} ownerStruct={ownerStruct} idx={idx} key={idx} inStock={inStock} isInPossession={isInPossession} />
            )}
        </WhiteBlock>
    );
};