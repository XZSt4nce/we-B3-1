import React from 'react';
import {Form} from "react-bootstrap";

export const Control = (
    {
        controlId,
        label,
        max,
        onChange,
        min,
        type = "text",
        step = 1,
        placeholder = `Введите Ваш ${label.toLowerCase()}`,
        required = true
    }
) => {
    return (
        <Form.Group controlId={controlId} className={"mb-3"}>
            <Form.Label>{label}{required && "*"}</Form.Label>
            <Form.Control type={type} min={min ?? 0} max={max} step={step} required={required} placeholder={placeholder} onChange={onChange} />
        </Form.Group>
    );
};