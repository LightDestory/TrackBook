import {NextFunction, Request, Response} from "express";
import Knex from "knex";
import db from "../db/database"
import {Sender} from "../utils/Sender";
import {Book} from "../types/book.type";
import {UserRead} from "../types/UserRead";

export function learnBooks(req: Request, res: Response, next: NextFunction) {
    if(req.method == "POST" && (Object.keys(req.body).length == 0)) {
        Sender.getInstance().sendError(res, Sender.ERROR_TYPE_PARAMETER);
        return;
    } else if(req.method == "GET" || (Object.keys(req.body.books).length == 0)) {
        next();
    } else {
        // @ts-ignore
        let con: Knex = db.getConnection();
        let data : UserRead[] = req.body.books;
        con('books')
            .select("isbn")
            .then((rows : Book[]) => {
                let library : string[] = rows.map((row : Book) => {return row.isbn});
                let user_isbns: string[] = data.map((book : UserRead) => { return book.isbn;});
                let unknown_books: string[] = user_isbns.filter((book : string) => !library.includes(book));
                if(unknown_books.length == 0) {
                    next();
                } else {
                    let newdata: UserRead[] = data.filter((book: Book) => unknown_books.includes(book.isbn));
                    con('books')
                        .insert(newdata.map((book: UserRead) => ({"isbn": book.isbn, "title": book.title}) ))
                        .then(() => {
                            next();
                        });
                }
            });
    }
}