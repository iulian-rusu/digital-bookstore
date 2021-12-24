import React, { Component } from 'react'
import './HomePage.css'

export default class HomePage extends Component {
    render() {
        return (
            <div className='HomePage'>
                <h1 id='welcomeMessage'>Welcome, ({this.props.token})</h1>
            </div>
        )
    }
}
