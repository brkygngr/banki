import { FormEvent, useState } from 'react';
import { Button, Col, Container, Form, Row } from 'react-bootstrap';
import { RegisterRequest } from '../../services/auth/authApi';
import { Link, NavigateFunction } from 'react-router-dom';

interface RegisterFormProps {
  isRegistering: boolean;
  onRegister: (request: RegisterRequest) => Promise<void>;
  navigate: NavigateFunction;
}

export function RegisterForm({ isRegistering, onRegister, navigate }: RegisterFormProps) {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    
    await onRegister({ username, email, password })

    navigate('/login');
  };

  return (
    <Container>
      <Row className="justify-content-md-center align-items-md-center h-100">
        <Col xs={12} md={6}>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Username</Form.Label>
              <Form.Control
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter username"
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Enter email"
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

            <Button className="mb-3" variant="primary" type="submit" disabled={isRegistering}>
              {isRegistering ? 'Registering...' : 'Register'}
            </Button>

            <p>
              Have an account? <Link to="/login">Login here</Link>
            </p>
          </Form>
        </Col>
      </Row>
    </Container>
  );
};
