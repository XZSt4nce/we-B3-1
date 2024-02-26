import React from 'react';

export const WhiteBlock = ({children, title, className}) => {
    return (
        <div className={`bg-white p-3 rounded mw-100 ${className}`}>
            <h1 className={"text-center"}>{title}</h1>
            {children}
        </div>
    );
};