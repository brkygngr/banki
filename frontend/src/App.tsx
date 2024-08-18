import 'bootstrap/dist/css/bootstrap.min.css';
import { Provider } from 'react-redux';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import './App.css';
import { AccountViewContainer } from './components/account/AccountViewContainer';
import { AccountsContainer } from './components/account/AccountsContainer';
import { LoginContainer } from './components/auth/LoginContainer';
import { RegisterContainer } from './components/auth/RegisterContainer';
import { Main } from './components/main/Main';
import { store } from './services/store';

function App() {
  return (
    <Provider store={store}>
      <BrowserRouter basename="/">
        <Routes>
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="/login" element={<LoginContainer />} />
          <Route path="/register" element={<RegisterContainer />} />
          <Route element={<Main />}>
            <Route path="/accounts" element={<AccountsContainer />} />
            <Route path="/accounts/:id" element={<AccountViewContainer />} />
          </Route>
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </Provider>
  );
}

export default App;
