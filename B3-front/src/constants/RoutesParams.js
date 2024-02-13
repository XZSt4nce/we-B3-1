import {User} from "../ui/components/User";

export const RoutesParams = [
    {
        path: "/users",
        indexPage: <p>404. Страница не найдена</p>,
        params: ":user",
        paramsPage: User
    }
];