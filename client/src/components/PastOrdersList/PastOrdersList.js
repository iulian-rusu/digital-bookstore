import React, { Component } from 'react'
import PastOrder from '../PastOrder/PastOrder'
import './PastOrdersList.css'

export default class PastOrdersList extends Component {
    constructor(props) {
        super(props)
    }

    render() {
        return (
            <div className='PastOrdersList flexColumn alignCenter'>
                <h1>Past Orders</h1>
                <table className='styledTable'>
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Date</th>
                            <th>Items</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        {this.props.pastOrders.map(
                            i => <PastOrder key={i.orderId} item={i} />
                        )}
                    </tbody>
                </table>
            </div>
        )
    }
}
