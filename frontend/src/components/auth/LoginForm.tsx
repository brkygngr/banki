import { FormEvent, useState } from 'react';
import { Button, Col, Container, Form, Row } from 'react-bootstrap';
import { Link, NavigateFunction } from 'react-router-dom';
import { LoginRequest } from '../../services/auth/authApi';

interface LoginFormProps {
  isLogging: boolean;
  onLogin: (request: LoginRequest) => Promise<void>;
  navigate: NavigateFunction;
}

export function LoginForm({
  isLogging,
  onLogin,
  navigate,
}: LoginFormProps) {
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    await onLogin({ identifier, password });

    navigate('/accounts');
  };

  return (
    <Container>
      <Row className="justify-content-md-center align-items-md-center h-100">
        <Col xs={12} md={6}>
          <h2>Login</h2>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>User</Form.Label>
              <Form.Control
                type="text"
                value={identifier}
                onChange={(e) => setIdentifier(e.target.value)}
                placeholder="Enter username or email"
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter password"
              />
            </Form.Group>

            <Button className="mb-3" variant="primary" type="submit" disabled={isLogging}>
              {isLogging ? 'Logging in...' : 'Login'}
            </Button>

            <p>
              Don't have an account yet? <Link to="/register">Register here</Link>
            </p>
          </Form>
        </Col>
      </Row>
    </Container>
  );
};
