import express, {Application, Request, Response} from 'express';
import {loginRoute} from './api/loginRoute';
import {userRoute} from './api/userRoute';

const app: Application = express();
app.use( express.json(), express.text() );

app.use('/login', loginRoute);

app.use('/user', userRoute);

app.get('/', (req: Request, res: Response) => {
    console.log("TRIGGERED GET");
    res.sendStatus(200);
});

app.listen(5000, () => {console.log("Listening to 5000!")});