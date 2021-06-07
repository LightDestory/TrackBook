import express, {Application, Request, Response} from 'express';
import {loginRoute} from './apis/loginRoute';
import {userRoute} from './apis/userRoute';
const configutation = require('dotenv').config();
const app: Application = express();


function bootstrap() {
    if(configutation.error){
        throw configutation.error;
    }
    console.log(configutation.parsed);
}

bootstrap()
/*app.use( express.json(), express.text() );

app.use('/login', loginRoute);

app.use('/user', userRoute);

app.get('/', (req: Request, res: Response) => {
    console.log("TRIGGERED GET");
    res.sendStatus(200);
});

app.listen(process.env.PORT, () => {console.log(`Listening to ${process.env.PORT}!`)});
*/