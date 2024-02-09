import React from 'react';

export const WhiteBlock = ({children, title}) => {
    return (
        <div className={"bg-white p-3 rounded mw-100"}>
            <h1 className={"text-center"}>{title}</h1>
            {children}
        </div>
    );
};