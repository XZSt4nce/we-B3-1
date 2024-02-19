import IndexPage from "../ui/pages/IndexPage";
import MainPage from "../ui/pages/MainPage";
import {UserPage} from "../ui/pages/UserPage";

export const Routes = [
    {
        path: "/",
        page: IndexPage
    },
    {
        path: "/main",
        page: MainPage
    },
    {
        path: "/user/:id",
        page: UserPage
    }
]