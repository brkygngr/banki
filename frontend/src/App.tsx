import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css'
import { Provider } from 'react-redux';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { LoginContainer } from './components/auth/LoginContainer';
import { RegisterContainer } from './components/auth/RegisterContainer';
import { store } from './services/store';

function App() {
  return (
    <Provider store={store}>
      <BrowserRouter>
        <Routes>
            <Route path="/login" element={<LoginContainer />} />
            <Route path="/register" element={<RegisterContainer />} />
        </Routes>
      </BrowserRouter>
    </Provider>
  );
}

export default App;
