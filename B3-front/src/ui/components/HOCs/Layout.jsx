import React, {useContext, useEffect} from 'react';
import {Context} from "../../../core/ContextWrapper";
import {Button, Navbar} from "react-bootstrap";
import {Link} from "react-router-dom";

export const Layout = ({children}) => {
    const {user, getContractValues, signOut} = useContext(Context);

    useEffect(() => {
        (async () => {
            await getContractValues();
        })();
    }, []);

    return (
        <div className={"d-flex flex-column w-100 h-100"}>
            <Navbar className={"p-2 d-flex"} style={{backgroundColor: "rebeccapurple"}}>
                <Navbar.Brand className={"text-white"} href="#home">Профессионалы</Navbar.Brand>
                <div className={"d-flex flex-grow-1 justify-content-end gap-3"}>
                    {!!user.login && (
                        <>
                            <Link className={"btn btn-primary"} to={"/main"}>Главная страница</Link>
                            <Button variant={"danger"} onClick={signOut}>Выход</Button>
                        </>
                    )}
                </div>
            </Navbar>
            <div className={"d-flex flex-column flex-grow-1 align-items-center p-3 gap-3 overflow-auto"}>
                {children}
            </div>
        </div>
    );
};