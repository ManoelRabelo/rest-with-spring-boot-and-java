import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './styles.css';

import api from '../../services/api'

import logoImage from '../../assets/logo.svg'
import padlock from '../../assets/padlock.png'

export default function Login() {

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const navigate = useNavigate();

    async function login(e) {
        e.preventDefault();

        const data = {
            username,
            password
        };

        try {
            const response = await api.post('auth/signin', data);

            localStorage.setItem('username', username);
            localStorage.setItem('accessToken', response.data.accessToken);
            localStorage.setItem('refreshToken', response.data.refreshToken);

            navigate("/books");
        } catch (error) {
            alert('Login failed! Try again!');
        }
    };

    return (
        <div className="login-container">
            <section className="form">
                <img src={logoImage} alt="Erudio Logo" />

                <form onSubmit={login}>
                    <h1>Acces yout Account</h1>
                    <input
                        placeholder='username'
                        value={username}
                        onChange={e => setUsername(e.target.value)}
                    />
                    <input
                        type="password" placeholder='password'
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                    />

                    <button className='button' type='submit'>Login</button>

                </form>
            </section>

            <img src={padlock} alt="Login" />
        </div>
    )
}