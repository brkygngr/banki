import { Container, Nav, Navbar } from "react-bootstrap";
import { Outlet, useNavigate } from "react-router-dom";
import { selectCurrentToken } from "../../services/auth/authSlice";
import { useSelector } from "react-redux";
import { NotificationDisplay } from "../toast/NotificationDisplay";

export function Main() {
  const token = useSelector(selectCurrentToken);
  const navigate = useNavigate();

  if (!token) {
    navigate("/login");
  }

  return (
    <div>
      <Navbar bg="primary" data-bs-theme="dark">
        <Container>
          <Nav>
            <Nav.Link href="/accounts" active>Accounts</Nav.Link>
          </Nav>
        </Container>
      </Navbar>
      <NotificationDisplay />
      <main>
        <Outlet />
      </main>
    </div>
  );
}
