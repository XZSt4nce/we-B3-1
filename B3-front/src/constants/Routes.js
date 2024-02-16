import IndexPage from "../ui/pages/IndexPage";
import MainPage from "../ui/pages/MainPage";
import {User} from "../ui/components/User";

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
        path: "/users/:id",
        page: User
    }
]