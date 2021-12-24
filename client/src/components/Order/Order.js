import React, { Component } from 'react'

export default class Order extends Component {
    constructor(props) {
        super(props)

        this.remove = () => {
            this.props.removeSelf(this.props.item.isbn)
        }
    }

    render() {
        return (
            <tr>
                <td>{this.props.item.isbn}</td>
                <td>{this.props.item.title}</td>
                <td>{this.props.item.quantity}</td>
                <td>{this.props.item.price}</td>
                <td><button className='brightButton' onClick={this.remove}>Remove</button></td>
            </tr>
        )
    }
}
